package com.nail.core.loadbalance;

public class DefaultLoadbalanceFactory implements LoadbalanceFactory {
    @Override
    public Loadbalancer getLoadbalancer(String groupName) {
        return new RandomLoadbalancer();
    }
}
