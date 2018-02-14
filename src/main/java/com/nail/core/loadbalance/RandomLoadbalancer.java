package com.nail.core.loadbalance;

import com.nail.core.registry.Service;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class RandomLoadbalancer implements Loadbalancer {
    @Override
    public Service choose(Map<String, Service> tmap, Object key) {
        int len = tmap.size();
        return tmap.get(new ArrayList<>(tmap.keySet()).get(new Random().nextInt(len)));
    }
}
