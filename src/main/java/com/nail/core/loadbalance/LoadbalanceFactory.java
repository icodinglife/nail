package com.nail.core.loadbalance;

public interface LoadbalanceFactory {
    Loadbalancer getLoadbalancer(String groupName);
}
