package com.nail.core;

import com.google.common.net.HostAndPort;

import java.util.ArrayList;
import java.util.List;

public class NailConfig {
    private String zone;
    private String name;
    private String host;
    private int port;
    private int parrallelism;
    private String zkAddr;

    public NailConfig() {
    }

    public NailConfig(String zone, String name, String host, int port, int parrallelism, String zkAddr) {
        this.zone = zone;
        this.name = name;
        this.host = host;
        this.port = port;
        this.parrallelism = parrallelism;
        this.zkAddr = zkAddr;
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

    public String getZkAddr() {
        return zkAddr;
    }

    public void setZkAddr(String zkAddr) {
        this.zkAddr = zkAddr;
    }

    public List<HostAndPort> getZKHosts() {
        List<HostAndPort> list = new ArrayList<>();

        String[] hosts = zkAddr.split(";");
        for (String host : hosts) {
            String[] host$port = host.split(":");
            HostAndPort hp = HostAndPort.fromParts(host$port[0], Integer.parseInt(host$port[1]));
            list.add(hp);
        }

        return list;
    }
}
