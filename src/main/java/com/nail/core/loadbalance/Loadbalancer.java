package com.nail.core.loadbalance;

import com.nail.core.registry.Service;

import java.util.Map;

public interface Loadbalancer {
    Service choose(Map<String, Service> serviceMap, Object key);
}
