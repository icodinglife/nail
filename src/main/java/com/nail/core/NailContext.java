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

    private FiberScheduler fiberScheduler;

    private AtomicBoolean inited = new AtomicBoolean(false);

    public void init(String name, int parallelism) {
        if (!inited.compareAndSet(false, true)) {
            logger.warn("already inited, do nothing ...");
            return;
        }

        fiberScheduler = new FiberForkJoinScheduler(name, parallelism, null, false);
    }

    public FiberScheduler getFiberScheduler() {
        return fiberScheduler;
    }
}
