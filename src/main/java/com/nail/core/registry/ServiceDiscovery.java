package com.nail.core.registry;

import com.google.common.net.HostAndPort;
import com.nail.core.NailContext;
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
        boolean result = registry.register(NailContext.ZKROOT, NailContext.ZKNODES, zone, nodeName, null, HostAndPort.fromParts(host, port), new RegistryData());
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

    private static class NodeListener implements DiscoveryListener {
        private ServiceDiscovery serviceDiscovery;

        public NodeListener(ServiceDiscovery serviceDiscovery) {
            this.serviceDiscovery = serviceDiscovery;
        }

        @Override
        public void onChange(String path, byte[] data, EvtType evtType) {
            switch (evtType) {

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

        }
    }
}
