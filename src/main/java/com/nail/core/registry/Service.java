package com.nail.core.registry;

/**
 * Created by guofeng.qin on 2018/02/09.
 */
public class Service {
    private String groupName;
    private String serviceName;
    private String nodeName;

    public Service() {
    }

    public Service(String groupName, String serviceName, String nodeName) {
        this.groupName = groupName;
        this.serviceName = serviceName;
        this.nodeName = nodeName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }
}
