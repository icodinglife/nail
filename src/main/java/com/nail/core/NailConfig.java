package com.nail.core;

public class NailConfig {
    private String name;
    private String host;
    private int port;
    private int parrallelism;

    public NailConfig() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getParrallelism() {
        return parrallelism;
    }

    public void setParrallelism(int parrallelism) {
        this.parrallelism = parrallelism;
    }
}
