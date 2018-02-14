package com.nail.core.quasar;

import co.paralleluniverse.actors.ActorRef;
import co.paralleluniverse.actors.behaviors.Server;
import co.paralleluniverse.fibers.Suspendable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

public class ProxyServer<CallMessage, V, CastMessage> extends Server<CallMessage, V, CastMessage> {

    private Map<String, Method> methodMap;
    private InvocationHandler invocationHandler;

    public ProxyServer(ActorRef<Object> actor) {
        super(actor);
    }

    public ProxyServer(ActorRef<Object> actor, InvocationHandler invocationHandler, Map<String, Method> methodMap) {
        this(actor);
        this.invocationHandler = invocationHandler;
        this.methodMap = methodMap;
    }

    @Suspendable
    public Object outInvoke(String method, Object[] params) throws Throwable {
        Method mtd = methodMap.get(method);
        if (mtd == null) {
            throw new RuntimeException("Method Not Found");
        }
        return invocationHandler.invoke(this, mtd, params);
    }
}
