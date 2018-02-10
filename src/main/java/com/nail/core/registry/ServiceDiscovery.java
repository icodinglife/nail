package com.nail.core.registry;

import com.alibaba.fastjson.JSON;
import com.google.common.net.HostAndPort;
import com.nail.core.NailContext;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by guofeng.qin on 2018/02/09.
 */
public class ServiceDiscovery {
    private static final Logger logger = LoggerFactory.getLogger(ServiceDiscovery.class);

    private Map<String, Node> nodeMap = new ConcurrentHashMap<>();
    private Map<String, ServiceGroup> serviceMap = new ConcurrentHashMap<>();

    private DiscoveryListener nodeListener;
    private DiscoveryListener serviceListener;

    private Registry registry;

    public ServiceDiscovery() {
    }

    public void init(Registry registry, String zone, String nodeName, String host, int port) {
        this.registry = registry;
        this.nodeListener = new NodeListener(this);
        this.serviceListener = new ServiceListener(this);

        registerSelfNode(zone, nodeName, host, port);

        addNodesListener();

        addServiceListener();
    }

    private void registerSelfNode(String zone, String nodeName, String host, int port) {
        Node node = new Node(nodeName, host, port);
        boolean result = registry.register(NailContext.ZKROOT, NailContext.ZKNODES, zone, nodeName, null, HostAndPort.fromParts(host, port), JSON.toJSONString(node).getBytes());
        if (!result) {
            throw new RuntimeException("Node Registry Error.");
        }
    }

    private void addNodesListener() {
        registry.addTreeListener(Helper.joinPath(NailContext.ZKROOT, NailContext.ZKNODES), nodeListener);
    }

    private void addServiceListener() {
        registry.addTreeListener(Helper.joinPath(NailContext.ZKROOT, NailContext.ZKSERVICES), serviceListener);
    }

    private boolean addNode(String nodeName, Node node) {
        if (nodeMap.containsKey(nodeName)) {
            logger.error(nodeName + " Node Already Exist.");
            return false;
        }
        nodeMap.put(nodeName, node);
        return true;
    }

    private void removeNode(String nodeName) {
        nodeMap.remove(nodeName);
    }

    private boolean addService(String groupName, String serviceName, Service service) {
        ServiceGroup serviceGroup = serviceMap.get(groupName);
        if (serviceGroup == null) {
            serviceGroup = new ServiceGroup(groupName);
            ServiceGroup oldGroup = serviceMap.putIfAbsent(groupName, serviceGroup);
            if (oldGroup != null) {
                serviceGroup = oldGroup;
            }
        }
        return serviceGroup.addService(serviceName, service);
    }

    private void removeService(String groupName, String serviceName) {
        ServiceGroup serviceGroup = serviceMap.get(groupName);
        if (serviceGroup != null) {
            serviceGroup.removeService(serviceName);
        }
    }

    private static class NodeListener implements DiscoveryListener {
        private ServiceDiscovery serviceDiscovery;

        public NodeListener(ServiceDiscovery serviceDiscovery) {
            this.serviceDiscovery = serviceDiscovery;
        }

        @Override
        public void onChange(String path, byte[] data, EvtType evtType) {
            String nodeName = null;
            Node node = null;
            if (data != null && data.length > 0) {
                node = JSON.parseObject(String.valueOf(data), Node.class);
                nodeName = node.getName();
            } else {
                nodeName = Helper.parseNodeName(path);
            }
            switch (evtType) {
                case CHILD_ADDED:
                case CHILD_UPDATED:
                    serviceDiscovery.addNode(nodeName, node);
                    break;
                case CHILD_REMOVED:
                    serviceDiscovery.removeNode(nodeName);
                    break;
                default:
                    break;
            }
        }
    }

    private static class ServiceListener implements DiscoveryListener {
        private ServiceDiscovery serviceDiscovery;

        public ServiceListener(ServiceDiscovery serviceDiscovery) {
            this.serviceDiscovery = serviceDiscovery;
        }

        @Override
        public void onChange(String path, byte[] data, EvtType evtType) {
            String groupName = null;
            String serviceName = null;
            Service service = null;
            if (data != null && data.length > 0) {
                service = JSON.parseObject(String.valueOf(data), Service.class);
                groupName = service.getGroupName();
                serviceName = service.getServiceName();
            } else {
                Pair<String, String> pair = Helper.parseService(path);
                groupName = pair.getLeft();
                serviceName = pair.getRight();
            }
            switch (evtType) {
                case CHILD_ADDED:
                case CHILD_UPDATED:
                    serviceDiscovery.addService(groupName, serviceName, service);
                    break;
                case CHILD_REMOVED:
                    serviceDiscovery.removeService(groupName, serviceName);
                    break;
                default:
                    break;
            }
        }
    }
}
