package com.nail.core.registry.zk;

import org.apache.curator.RetryPolicy;
import org.apache.curator.RetrySleeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by guofeng.qin on 2018/02/08.
 */
public class ForeverRetryPolicy implements RetryPolicy {
    private static final Logger logger = LoggerFactory.getLogger(ForeverRetryPolicy.class);

    private int retryTimeStep;
    private int maxRetryTime;

    public ForeverRetryPolicy(int retryTimeStep, int maxRetryTime) {
        this.retryTimeStep = retryTimeStep;
        this.maxRetryTime = maxRetryTime;
    }

    @Override
    public boolean allowRetry(int retryCount, long elapsedTimeMs, RetrySleeper sleeper) {
        try {
            sleeper.sleepFor(Math.min(Math.abs((retryCount + 1) * retryTimeStep), maxRetryTime), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.error("Retry Sleep Interrupted...", e);
            return false;
        }
        return true;
    }
}
