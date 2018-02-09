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
        if (pathChildrenCacheMap.containsKey(path)) {
            logger.warn(path + " zk listener already exist.");
            return;
        }

        PathChildrenCache pathChildrenCache = new PathChildrenCache(curatorFramework, path, true);

        pathChildrenCache.getListenable().addListener((curatorFramework, event) -> {
            Debug.debug(() -> logger.info("Rrecv ZK event: " + JSON.toJSONString(event)));

            byte[] data = null;
            if (event.getData() != null) {
                data = event.getData().getData();
            }

            listener.onChange(path, data, DiscoveryListener.switchType(event.getType()));
        });

        try {
            if (pathChildrenCacheMap.putIfAbsent(path, pathChildrenCache) == null) {
                pathChildrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
            } else {
                pathChildrenCache.close();
            }
        } catch (Exception e) {
            logger.error("ZK AddListener Error...", e);
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
