package com.nail.core.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by guofeng.qin on 2018/02/09.
 */
public class ServiceGroup {
    private String name;
    private Map<String, Service> serviceMap;

    public ServiceGroup(String name) {
        this.name = name;
        serviceMap = new ConcurrentHashMap<>();
    }

    public String getName() {
        return this.name;
    }

    public boolean addService(String serviceName, Service service) {
        serviceMap.put(serviceName, service);
        return true;
    }

    public void removeService(String serviceName) {
        serviceMap.remove(serviceName);
    }

    public Service getService(String serviceName) {
        return serviceMap.get(serviceName);
    }

    public Map<String, Service> getServiceMap() {
        return serviceMap;
    }
}
