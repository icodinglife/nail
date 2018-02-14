package com.nail.core;

import co.paralleluniverse.actors.ActorRef;
import co.paralleluniverse.fibers.FiberUtil;
import com.nail.core.entity.RemoteMessage;
import com.nail.core.entity.RemoteRequest;
import com.nail.core.entity.RemoteResponse;
import com.nail.core.loadbalance.LoadbalanceManager;
import com.nail.core.loadbalance.Loadbalancer;
import com.nail.core.quasar.ProxyServer;
import com.nail.core.registry.Node;
import com.nail.core.registry.Service;
import com.nail.core.registry.ServiceDiscovery;
import com.nail.core.registry.ServiceGroup;
import com.nail.core.transport.ITransClient;
import com.nail.core.transport.TransManager;
import com.nail.utils.KryoHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

public class RemoteManager {
    private static final Logger logger = LoggerFactory.getLogger(RemoteManager.class);

    private LoadbalanceManager loadbalanceManager;
    private NailContext nailContext;
    private ServiceDiscovery serviceDiscovery;
    private TransManager transManager;
    private ServiceManager serviceManager;

    private AtomicInteger reqIndex = new AtomicInteger(0);

    private Map<String, CompletableFuture<? super Object>> futureMap;

    public void init(NailContext nailContext, ServiceDiscovery serviceDiscovery, LoadbalanceManager manager, TransManager transManager, ServiceManager serviceManager) {
        this.nailContext = nailContext;
        this.loadbalanceManager = manager;
        this.serviceDiscovery = serviceDiscovery;
        this.transManager = transManager;
        this.serviceManager = serviceManager;

        futureMap = new ConcurrentHashMap<>();
    }

    public void sendRequestTo(String group, Method method, Object[] args, CompletableFuture<? super Object> finished) {
        Loadbalancer loadbalancer = loadbalanceManager.getLoadbalancer(group);
        ServiceGroup serviceGroup = serviceDiscovery.getServiceGroup(group);
        Service service = loadbalancer.choose(serviceGroup.getServiceMap(), args);
        if (service == null) {
            finished.completeExceptionally(new RuntimeException("Service Not Exist."));
            return;
        }
        String nodeName = service.getNodeName();
        if (StringUtils.isEmpty(nodeName)) {
            finished.completeExceptionally(new RuntimeException("Service Node Not Exist.`"));
            return;
        }
        Node node = serviceDiscovery.getNode(nodeName);
        if (node == null) {
            finished.completeExceptionally(new RuntimeException("Service Node Not Exist."));
            return;
        }

        // Send Message To Remote Node
        String reqId = genReqId();

        if (finished != null) {
            futureMap.put(reqId, finished);
        }

        RemoteRequest remoteRequest = new RemoteRequest(reqId, group, service.getServiceName(), method.getName(), args);
        ITransClient transClient = transManager.getTransClient(node.getHost(), node.getPort());
        boolean transResult = transClient.trans(RemoteMessage.Type.REQUEST, KryoHelper.writeClassAndObject(remoteRequest));
        if (!transResult) {
            futureMap.remove(reqId);
            finished.completeExceptionally(new RuntimeException("Remote Send Error."));
        }
    }

    private String genReqId() {
        return StringUtils.join(nailContext.getName(), reqIndex.addAndGet(1), '-');
    }

    public void recvRequest(RemoteRequest request) throws ExecutionException, InterruptedException {
        String id = request.getId();
        String serviceGroup = request.getServiceGroup();
        String serviceName = request.getServiceName();
        String method = request.getMethodName();
        Object[] params = request.getParams();
        FiberUtil.runInFiber(() -> {
            ActorRef serviceRef = serviceManager.getService(serviceGroup, serviceName);
            ProxyServer proxyServer = (ProxyServer) serviceRef;
            try {
                Object result = proxyServer.outInvoke(method, params);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    public void recvResponse(RemoteResponse response) {
        String id = response.getId();
        CompletableFuture<Object> future = futureMap.remove(id);
        if (future == null) {
            logger.error("Missing Future of " + id);
            return;
        }
        switch (response.getStatus()) {
            case SUCCESS:
                future.complete(response.getContent());
                break;
            case EXCEPTION:
                future.completeExceptionally((Exception) response.getContent());
                break;
            case ERROR:
                future.completeExceptionally(new RuntimeException(response.getMsg()));
                break;
            case UNKNOWN:
                future.completeExceptionally(new RuntimeException("Unknown Exception"));
                break;
        }
    }
}
