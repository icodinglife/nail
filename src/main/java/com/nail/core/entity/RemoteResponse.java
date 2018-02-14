package com.nail.core.entity;

public class RemoteResponse extends RemoteMessage {
    private static final long serialVersionUID = 20180214;

    private Status status;
    private Object content;
    private String msg;

    public RemoteResponse() {
    }

    public RemoteResponse(String id, Status status, Object content, String msg) {
        super(id);
        this.status = status;
        this.content = content;
        this.msg = msg;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static enum Status {

        UNKNOWN(-1),
        SUCCESS(0),
        EXCEPTION(1),
        ERROR(2);

        private int _val;

        private Status(int v) {
            _val = v;
        }

        public int getVal() {
            return _val;
        }
    }
}
