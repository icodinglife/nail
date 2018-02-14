package com.nail.core.entity;

public class RemoteMessage implements java.io.Serializable {
    private static final long serialVersionUID = 20180214;

    private String id;

    public RemoteMessage() {
    }

    public RemoteMessage(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static enum Type {
        UNKNOWN(-1),
        REQUEST(1),
        RESPONSE(2);

        private int _val;

        private Type(int v) {
            _val = v;
        }

        public int getVal() {
            return _val;
        }

        public static Type fromVal(int v) {
            switch (v) {
                case 1:
                    return REQUEST;
                case 2:
                    return RESPONSE;
                default:
                    return UNKNOWN;
            }
        }
    }
}
