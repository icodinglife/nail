package com.nail.core;


import co.paralleluniverse.actors.ActorRef;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceManager {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ServiceManager.class);

    private Map<String, Map<String, ActorRef>> serviceMap;

    public void init() {
        serviceMap = new ConcurrentHashMap<>();
    }

    public ActorRef getService(String group, String name) {
        Map<String, ActorRef> map = serviceMap.get(group);
        if (map != null) {
            return map.get(name);
        }
        return null;
    }
}
