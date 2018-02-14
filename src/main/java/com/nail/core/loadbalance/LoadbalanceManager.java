package com.nail.core.loadbalance;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LoadbalanceManager {

    private LoadbalanceFactory defaultLoadbalanceFactory;

    private Map<String, LoadbalanceFactory> factoryMap;

    public void init() {
        factoryMap = new ConcurrentHashMap<>();
        defaultLoadbalanceFactory = new DefaultLoadbalanceFactory();
    }

    public Loadbalancer getLoadbalancer(String group) {
        LoadbalanceFactory factory = factoryMap.get(group);
        if (factory == null) {
            return defaultLoadbalanceFactory.getLoadbalancer(group);
        }

        return factory.getLoadbalancer(group);
    }
}
