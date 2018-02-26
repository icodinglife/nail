package com.nail.core;


import co.paralleluniverse.actors.ActorRef;
import co.paralleluniverse.fibers.FiberFactory;
import co.paralleluniverse.fibers.Suspendable;
import com.nail.core.quasar.ProxyActor;
import com.nail.core.registry.Service;
import com.nail.core.registry.ServiceDiscovery;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceManager {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ServiceManager.class);

    private NailContext nailContext;
    private ServiceDiscovery serviceDiscovery;

    private NailConfig nailConfig;

    private Map<String, Map<String, ActorRef<Object>>> serviceMap;

    public void init(NailContext nailContext, NailConfig nailConfig, ServiceDiscovery serviceDiscovery) {
        this.nailContext = nailContext;
        this.nailConfig = nailConfig;
        this.serviceDiscovery = serviceDiscovery;

        serviceMap = new ConcurrentHashMap<>();
    }

    public ActorRef getService(String group, String name) {
        Map<String, ActorRef<Object>> map = serviceMap.get(group);
        if (map != null) {
            return map.get(name);
        }
        return null;
    }

    public void deployService(Object target) {
        Class<?> clazz = target.getClass();

        checkService(clazz);

        String name = StringUtils.join(new String[]{clazz.getName(), "" + target.hashCode()}, '@');

        ProxyActor proxy = new ProxyActor(name, true, target);
        ActorRef ref = proxy.spawn((FiberFactory) nailContext.getFiberScheduler());

        deployService(name, ref, clazz);
    }

    private void checkService(Class<?> target) {
        if (!Closeable.class.isAssignableFrom(target)) {
            throw new RuntimeException("Service Interface Must Implement java.io.Closeable.");
        }
        Suspendable suspendable = target.getDeclaredAnnotation(Suspendable.class);
        if (suspendable == null) {
            Class<?>[] ifaces = target.getInterfaces();
            if (ifaces != null && ifaces.length > 0) {
                for (Class<?> iface : ifaces) {
                    suspendable = iface.getDeclaredAnnotation(Suspendable.class);
                    if (suspendable != null) {
                        return;
                    }
                }
            }
        }
        if (suspendable == null) {
            throw new RuntimeException("Service Interface Must Add @Suspendable.");
        }
    }

    public void undeployService(Object target) {
        Class<?> clazz = target.getClass();
        String name = StringUtils.join(clazz.getName(), target.hashCode(), '@');

        undeployService(name, clazz);
    }

    private void deployService(String name, ActorRef ref, Class<?> clazz) {
        Service service = wrapService(name, clazz);
        serviceDiscovery.registerService(nailConfig.getZone(), service);
//        nailContext.addActorRef(clazz.getName(), name, ref);
        addActorRef(Utils.getGroupName(clazz), name, ref);
    }

    private void addActorRef(String group, String name, ActorRef<Object> ref) {
        Map<String, ActorRef<Object>> map = serviceMap.get(group);
        if (map == null) {
            map = new ConcurrentHashMap<>();
            Map<String, ActorRef<Object>> oldMap = serviceMap.putIfAbsent(group, map);
            if (oldMap != null) {
                map = oldMap;
            }
        }
        map.put(name, ref);
    }

    private Service wrapService(String name, Class<?> clazz) {
        String serviceGroup = Utils.getGroupName(clazz);
        Service service = new Service(serviceGroup, name, nailContext.getName());
        return service;
    }

    private void undeployService(String name, Class<?> clazz) {
        Service service = wrapService(name, clazz);
        serviceDiscovery.unregisterService(nailConfig.getZone(), service);
    }
}
