package com.nail.core.registry.zk;

import com.alibaba.fastjson.JSON;
import com.google.common.net.HostAndPort;
import com.nail.core.registry.Discovery;
import com.nail.core.registry.DiscoveryListener;
import com.nail.debug.Debug;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by guofeng.qin on 2018/02/08.
 */
public class ZKDiscovery implements Discovery {
    private static final Logger logger = LoggerFactory.getLogger(ZKDiscovery.class);

    private CuratorFramework curatorFramework;
    private ConcurrentHashMap<String, PathChildrenCache> pathChildrenCacheMap;

    @Override
    public void init(List<HostAndPort> zkHosts) {
        if (zkHosts == null || zkHosts.size() <= 0) {
            throw new RuntimeException("Zookeeper hosts is null...");
        }

        pathChildrenCacheMap = new ConcurrentHashMap<>();

        List<String> hostList = new ArrayList<>();
        for (HostAndPort hp : zkHosts) {
            hostList.add(hp.getHost() + ":" + hp.getPort());
        }
        String hosts = StringUtils.join(hostList, ',');

        RetryPolicy retryPolicy = new ForeverRetryPolicy(1000, 60 * 1000);

        curatorFramework = CuratorFrameworkFactory.newClient(hosts, 1000 * 10, 1000 * 3, retryPolicy);

        curatorFramework.start();
    }

    @Override
    public void addListener(String path, DiscoveryListener listener) {
        Objects.requireNonNull(listener, "listener cannot be null");
        Objects.requireNonNull(curatorFramework, "not call init");

        PathChildrenCache pathChildrenCache = new PathChildrenCache(curatorFramework, path, true);

        pathChildrenCache.getListenable().addListener((curatorFramework, event) -> {
            Debug.debug(() -> logger.info("Rrecv ZK event: " + JSON.toJSONString(event)));

            listener.onChange(path, event.getData().getData(), switchType(event.getType()));
        });

        try {
            pathChildrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
            pathChildrenCacheMap.put(path, pathChildrenCache);
        } catch (Exception e) {
            logger.error("ZK AddListener Error...", e);
        }
    }

    public DiscoveryListener.EvtType switchType(PathChildrenCacheEvent.Type type) {
        switch (type) {
            case CHILD_ADDED:
                return DiscoveryListener.EvtType.CHILD_ADDED;
            case CHILD_UPDATED:
                return DiscoveryListener.EvtType.CHILD_UPDATED;
            case CHILD_REMOVED:
                return DiscoveryListener.EvtType.CHILD_REMOVED;
            case CONNECTION_SUSPENDED:
                return DiscoveryListener.EvtType.CONNECTION_SUSPENDED;
            case CONNECTION_RECONNECTED:
                return DiscoveryListener.EvtType.CONNECTION_RECONNECTED;
            case CONNECTION_LOST:
                return DiscoveryListener.EvtType.CONNECTION_LOST;
            case INITIALIZED:
                return DiscoveryListener.EvtType.INITIALIZED;
            default:
                return DiscoveryListener.EvtType.UNKNOWN;
        }
    }

    @Override
    public void close() throws IOException {
        for (Map.Entry<String, PathChildrenCache> entry : pathChildrenCacheMap.entrySet()) {
            try {
                entry.getValue().close();
            } catch (Exception e) {
                logger.error(entry.getKey() + " PathChildrenCache Close Error", e);
            }
        }

        pathChildrenCacheMap.clear();
        pathChildrenCacheMap = null;

        curatorFramework.close();
    }
}
