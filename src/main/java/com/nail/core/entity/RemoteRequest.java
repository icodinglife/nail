package com.nail.core.entity;

public class RemoteRequest extends RemoteMessage {
    private static final long serialVersionUID = 20180214;

    private String sourceNode;
    private String serviceGroup;
    private String serviceName;
    private String methodName;
    private Object[] params;

    public RemoteRequest() {
    }

    public RemoteRequest(String sourceNode, String reqId, String serviceGroup, String serviceName, String methodName, Object[] params) {
        super(reqId);
        this.sourceNode = sourceNode;
        this.serviceGroup = serviceGroup;
        this.serviceName = serviceName;
        this.methodName = methodName;
        this.params = params;
    }

    public String getServiceGroup() {
        return serviceGroup;
    }

    public void setServiceGroup(String serviceGroup) {
        this.serviceGroup = serviceGroup;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public String getSourceNode() {
        return sourceNode;
    }

    public void setSourceNode(String sourceNode) {
        this.sourceNode = sourceNode;
    }
}
