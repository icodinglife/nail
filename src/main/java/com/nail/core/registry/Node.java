package com.nail.core.registry;

/**
 * Created by guofeng.qin on 2018/02/09.
 */
public class Node {
    private String name;
    private String host;
    private int port;

    public Node() {
    }

    public Node(String name, String host, int port) {
        this.name = name;
        this.host = host;
        this.port = port;
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
}
