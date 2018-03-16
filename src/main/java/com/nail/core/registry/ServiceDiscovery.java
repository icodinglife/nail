package com.nail.core.registry;

import com.alibaba.fastjson.JSON;
import com.nail.core.NailContext;
import com.nail.core.transport.TransManager;
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
    private TransManager transManager;

    public ServiceDiscovery() {
    }

    public void init(Registry registry, TransManager transManager, String zone, String nodeName, String host, int port) {
        this.registry = registry;
        this.transManager = transManager;
        this.nodeListener = new NodeListener(this);
        this.serviceListener = new ServiceListener(this);

        registerSelfNode(zone, nodeName, host, port);

        addNodesListener();

        addServiceListener();
    }

    private void registerSelfNode(String zone, String nodeName, String host, int port) {
        Node node = new Node(nodeName, host, port);
        boolean result = registry.register(NailContext.ZKROOT, NailContext.ZKNODES, zone, nodeName, null, JSON.toJSONString(node).getBytes());
        if (!result) {
            throw new RuntimeException("Node Registry Error.");
        }
    }

    public void registerService(String zone, Service service) {
        boolean result = registry.register(NailContext.ZKROOT, NailContext.ZKSERVICES, zone, service.getGroupName(), service.getServiceName(), JSON.toJSONString(service).getBytes());
        if (!result) {
            throw new RuntimeException("Service Registry Error.");
        }
    }

    public void unregisterService(String zone, Service service) {
        registry.unregister(NailContext.ZKROOT, NailContext.ZKSERVICES, zone, service.getGroupName(), service.getServiceName());
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
            return updateNode(nodeName, node);
        }
        nodeMap.put(nodeName, node);
        return true;
    }

    private boolean updateNode(String nodeName, Node node) {
        Node oldNode = nodeMap.get(nodeName);
        if (oldNode.equals(node)) {
            return true;
        }
        transManager.removeTransClient(oldNode.getHost(), oldNode.getPort());
        nodeMap.put(nodeName, node);
        return true;
    }

    private void removeNode(String nodeName) {
        Node node = nodeMap.remove(nodeName);
        if (node != null) {
            transManager.removeTransClient(node.getHost(), node.getPort());
        }
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

    public Node getNode(String nodeName) {
        return nodeMap.get(nodeName);
    }

    public ServiceGroup getServiceGroup(String groupName) {
        return serviceMap.get(groupName);
    }

    public Service getService(String groupName, String serviceName) {
        ServiceGroup group = getServiceGroup(groupName);
        if (group != null) {
            return group.getService(serviceName);
        }
        return null;
    }

    private static class NodeListener implements DiscoveryListener {
        private ServiceDiscovery serviceDiscovery;

        public NodeListener(ServiceDiscovery serviceDiscovery) {
            this.serviceDiscovery = serviceDiscovery;
        }

        @Override
        public void onChange(String path, byte[] data, EvtType evtType) {
            if (data == null || data.length <= 0) {
                return;
            }
            String nodeName = null;
            Node node = null;
            if (data != null && data.length > 0) {
                node = JSON.parseObject(new String(data), Node.class);
                nodeName = node.getName();
            } else {
                nodeName = Helper.parseNodeName(path);
            }
            switch (evtType) {
                case INITIALIZED:
                    break;
                case NODE_ADDED:
                case NODE_UPDATED:
                    serviceDiscovery.addNode(nodeName, node);
                    break;
                case NODE_REMOVED:
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
            if (data == null || data.length <= 0) {
                return;
            }
            String groupName = null;
            String serviceName = null;
            Service service = null;
            if (data != null && data.length > 0) {
                service = JSON.parseObject(new String(data), Service.class);
                groupName = service.getGroupName();
                serviceName = service.getServiceName();
            } else {
                Pair<String, String> pair = Helper.parseService(path);
                groupName = pair.getLeft();
                serviceName = pair.getRight();
            }
            switch (evtType) {
                case INITIALIZED:
                    break;
                case NODE_ADDED:
                case NODE_UPDATED:
                    serviceDiscovery.addService(groupName, serviceName, service);
                    break;
                case NODE_REMOVED:
                    serviceDiscovery.removeService(groupName, serviceName);
                    break;
                default:
                    break;
            }
        }
    }
}
