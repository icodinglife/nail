package com.nail.core;

import co.paralleluniverse.actors.ActorRef;
import co.paralleluniverse.fibers.FiberForkJoinScheduler;
import co.paralleluniverse.fibers.FiberScheduler;
import io.netty.channel.nio.NioEventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * main app context
 */
public class NailContext {
    private static final Logger logger = LoggerFactory.getLogger(NailContext.class);

    public static final String ZKROOT = "/nail";
    public static final String ZKNODES = "nodes";
    public static final String ZKSERVICES = "services";

    private NailConfig config;

    private FiberScheduler fiberScheduler;

    private AtomicBoolean inited = new AtomicBoolean(false);

    private Map<String, Map<String, ActorRef<Object>>> actorRefMap;

    private Executor serverExecutor;
    private Executor clientExecutor;

    public void init(NailConfig config) {
        if (!inited.compareAndSet(false, true)) {
            logger.warn("already inited, do nothing ...");
            return;
        }

        this.config = config;

        fiberScheduler = new FiberForkJoinScheduler(config.getName() + "-pool", config.getParrallelism(), null, false);

        actorRefMap = new ConcurrentHashMap<>();

        serverExecutor = new NioEventLoopGroup(1);
        clientExecutor = new NioEventLoopGroup(2);
    }

    public FiberScheduler getFiberScheduler() {
        return fiberScheduler;
    }

    public String getName() {
        return null;
    }

    public void addActorRef(String group, String name, ActorRef<Object> ref) {
        Map<String, ActorRef<Object>> map = actorRefMap.get(group);
        if (map == null) {
            map = new ConcurrentHashMap<>();
            Map<String, ActorRef<Object>> oldMap = actorRefMap.putIfAbsent(group, map);
            if (oldMap != null) {
                map = oldMap;
            }
        }
        map.put(name, ref);
    }

    public void removeActor(String group, String name) {
        Map<String, ActorRef<Object>> map = actorRefMap.get(group);
        if (map != null) {
            map.remove(name);
        }
    }

    public Executor getServerExecutor() {
        return serverExecutor;
    }

    public Executor getClientExecutor() {
        return clientExecutor;
    }
}
