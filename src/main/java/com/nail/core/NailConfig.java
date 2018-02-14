package com.nail.core;

public class NailConfig {
    private String zone;
    private String name;
    private String host;
    private int port;
    private int parrallelism;

    public NailConfig() {
    }

    public NailConfig(String zone, String name, String host, int port, int parrallelism) {
        this.zone = zone;
        this.name = name;
        this.host = host;
        this.port = port;
        this.parrallelism = parrallelism;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
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
