package com.nail.core;


import co.paralleluniverse.actors.ActorRef;
import co.paralleluniverse.fibers.FiberFactory;
import com.nail.core.quasar.ProxyActor;
import com.nail.core.registry.Service;
import com.nail.core.registry.ServiceDiscovery;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceManager {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ServiceManager.class);

    private NailContext nailContext;
    private ServiceDiscovery serviceDiscovery;

    private NailConfig nailConfig;

    private Map<String, Map<String, ActorRef>> serviceMap;

    public void init(NailContext nailContext, NailConfig nailConfig, ServiceDiscovery serviceDiscovery) {
        this.nailContext = nailContext;
        this.nailConfig = nailConfig;
        this.serviceDiscovery = serviceDiscovery;

        serviceMap = new ConcurrentHashMap<>();
    }

    public ActorRef getService(String group, String name) {
        Map<String, ActorRef> map = serviceMap.get(group);
        if (map != null) {
            return map.get(name);
        }
        return null;
    }

    public void deployService(Object target) {
        Class<?> clazz = target.getClass();
        String name = StringUtils.join(clazz.getName(), target.hashCode(), '@');

        ProxyActor proxy = new ProxyActor(name, true, target);
        ActorRef ref = proxy.spawn((FiberFactory) nailContext.getFiberScheduler());

        deployService(name, ref, clazz);
    }

    public void undeployService(Object target) {
        Class<?> clazz = target.getClass();
        String name = StringUtils.join(clazz.getName(), target.hashCode(), '@');

        undeployService(name, clazz);
    }

    private void deployService(String name, ActorRef ref, Class<?> clazz) {
        Service service = wrapService(name, clazz);
        serviceDiscovery.registerService(nailConfig.getZone(), service);
        nailContext.addActorRef(clazz.getName(), name, ref);
    }

    private Service wrapService(String name, Class<?> clazz) {
        String serviceGroup = clazz.getName();
        Service service = new Service(serviceGroup, name, nailContext.getName());
        return service;
    }

    private void undeployService(String name, Class<?> clazz) {
        Service service = wrapService(name, clazz);
        serviceDiscovery.unregisterService(nailConfig.getZone(), service);
    }
}
