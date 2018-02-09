package com.nail.core;

import co.paralleluniverse.fibers.FiberForkJoinScheduler;
import co.paralleluniverse.fibers.FiberScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public void init(NailConfig config) {
        if (!inited.compareAndSet(false, true)) {
            logger.warn("already inited, do nothing ...");
            return;
        }

        this.config = config;

        fiberScheduler = new FiberForkJoinScheduler(config.getName() + "-pool", config.getParrallelism(), null, false);
    }

    public FiberScheduler getFiberScheduler() {
        return fiberScheduler;
    }
}
