package com.nail.core.registry;

/**
 * Created by guofeng.qin on 2018/02/08.
 */
public class RegistryData {
    private String node;
    private int weight;

    public RegistryData() {
    }

    public RegistryData(String node, int weight) {
        this.node = node;
        this.weight = weight;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
