package com.nail.core.registry.zk;

import com.alibaba.fastjson.JSON;
import com.google.common.net.HostAndPort;
import com.nail.core.registry.DiscoveryListener;
import com.nail.core.registry.Helper;
import com.nail.core.registry.Registry;
import com.nail.debug.Debug;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.zookeeper.CreateMode;
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
public class ZKRegistry implements Registry {
    private static final Logger logger = LoggerFactory.getLogger(ZKRegistry.class);

    private CuratorFramework curatorFramework;
    private ConcurrentHashMap<String, PathChildrenCache> pathChildrenCacheMap;
    private ConcurrentHashMap<String, TreeCache> treeCacheMap;

    @Override
    public void init(List<HostAndPort> zkHosts) {
        if (zkHosts == null || zkHosts.size() <= 0) {
            throw new RuntimeException("Zookeeper hosts is null...");
        }

        pathChildrenCacheMap = new ConcurrentHashMap<>();
        treeCacheMap = new ConcurrentHashMap<>();

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
    public boolean register(String namespace, String zone, String group, String server, String service, byte[] registryData) {
        Objects.requireNonNull(curatorFramework, "call init first...");

        String path = Helper.joinPath(namespace, zone, group, server, service);
        byte[] data = registryData;

        return register(path, data);
    }

    private boolean register(String path, byte[] data) {
        try {
            if (curatorFramework.checkExists().forPath(path) != null) {
                curatorFramework.delete().forPath(path);
            }
        } catch (Exception e) {
            logger.error(path + " ZK Delete Path Error", e);
        }

        try {
            curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path, data);
            addPathListener(path, new DiscoveryListener() {
                private boolean initd = false;

                @Override
                public void onChange(String path, byte[] dt, EvtType evtType) {
                    switch (evtType) {
                        case INITIALIZED:
                            initd = true;
                            break;
                        case CONNECTION_RECONNECTED:
                            if (!initd) {
                                return;
                            }
                            ZKRegistry.this.register(path, data);
                            break;
                    }
                }
            });
        } catch (Exception e) {
            logger.error(path + " ZK Register Error.", e);
            return false;
        }

        return true;
    }

    private void addPathListener(String path, DiscoveryListener listener) {
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
    public boolean unregister(String namespace, String zone, String group, String server, String service) {
        String path = Helper.joinPath(namespace, zone, group, server, service);

        PathChildrenCache pathChildrenCache = pathChildrenCacheMap.remove(path);
        if (pathChildrenCache != null) {
            try {
                pathChildrenCache.close();
            } catch (Exception e) {
                logger.error(path + " PathChildrenCache Close Error.", e);
            }
        }

        try {
            if (curatorFramework.checkExists().forPath(path) != null) {
                curatorFramework.delete().forPath(path);
            }
            return true;
        } catch (Exception e) {
            logger.error(path + " Delete Error.", e);
        }

        return false;
    }

    @Override
    public void addTreeListener(String path, DiscoveryListener listener) {
        if (treeCacheMap.containsKey(path)) {
            logger.warn(path + " zk tree listener already exist.");
            return;
        }

        TreeCache treeCache = new TreeCache(curatorFramework, path);
        treeCache.getListenable().addListener((client, event) -> {
            String ppath = path;
            byte[] data = null;
            if (event.getData() != null) {
                data = event.getData().getData();
                ppath = event.getData().getPath();
            }

            listener.onChange(ppath, data, DiscoveryListener.switchType(event.getType()));
        });
        try {
            if (treeCacheMap.putIfAbsent(path, treeCache) == null) {
                treeCache.start();
            } else {
                treeCache.close();
            }
        } catch (Exception e) {
            logger.error(path + " Add tree cache listener error.", e);
        }
    }

    @Override
    public void close() throws IOException {
        for (Map.Entry<String, PathChildrenCache> entry : pathChildrenCacheMap.entrySet()) {
            try {
                entry.getValue().close();
            } catch (Exception e) {
                logger.error(entry.getKey() + " PathChildrenCache Close Error.", e);
            }
        }

        for (Map.Entry<String, TreeCache> entry : treeCacheMap.entrySet()) {
            try {
                entry.getValue().close();
            } catch (Exception e) {
                logger.error(entry.getKey() + " TreeCache Close Error", e);
            }
        }

        pathChildrenCacheMap.clear();
        treeCacheMap.clear();

        curatorFramework.close();
    }
}
