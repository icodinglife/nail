package com.nail.core.registry.zk;

import com.alibaba.fastjson.JSON;
import com.google.common.net.HostAndPort;
import com.nail.core.NailContext;
import com.nail.core.registry.Registry;
import com.nail.core.registry.RegistryData;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by guofeng.qin on 2018/02/08.
 */
public class ZKRegistry implements Registry {
    private static final Logger logger = LoggerFactory.getLogger(ZKRegistry.class);

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
    public boolean register(String namespace, String zone, String group, String server, String service, HostAndPort host, RegistryData registryData) {
        Objects.requireNonNull(curatorFramework, "call init first...");

        String path = joinPath(namespace, zone, group, server, service);
        byte[] data = JSON.toJSONString(registryData).getBytes();

        try {
            if (curatorFramework.checkExists().forPath(path) != null) {
                curatorFramework.delete().forPath(path);
            }
        } catch (Exception e) {
            logger.error(path + " ZK Delete Path Error", e);
        }

        try {
            curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path, data);
            // TODO ... add listener
        } catch (Exception e) {
            logger.error(path + " ZK Register Error.", e);
            return false;
        }

        return true;
    }

    private String joinPath(String... strs) {
        List<String> pathList = new ArrayList<>();
        for (String str : strs) {
            if (!StringUtils.isEmpty(str)) {
                pathList.add(str);
            }
        }

        return StringUtils.join(pathList, '/');
    }

    @Override
    public boolean unregister(String namespace, String zone, String group, String server, String service, HostAndPort host) {
        return false;
    }

    @Override
    public void close() throws IOException {

    }
}
