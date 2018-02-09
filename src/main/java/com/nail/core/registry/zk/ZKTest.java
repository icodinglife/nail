package com.nail.core.registry.zk;

import com.alibaba.fastjson.JSON;
import com.google.common.net.HostAndPort;
import com.nail.core.registry.Registry;
import com.nail.core.registry.RegistryData;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by guofeng.qin on 2018/02/08.
 */
public class ZKTest {
    public static void testRegistry() {
        List<HostAndPort> list = new ArrayList<>();
        list.add(HostAndPort.fromParts("127.0.0.1", 2181));

        Registry registry = new ZKRegistry();

        registry.init(list);

        RegistryData data = new RegistryData("testnode", 10);
        registry.register("/nail", "wechat", "east", "UserService", "UserService1", null, data);

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void pathCacheTest() {
        final String zkHost = "127.0.0.1";
        final String path = "/tmp";
        RetryPolicy retryPolicy = new ForeverRetryPolicy(1000, 60 * 1000);
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(zkHost, retryPolicy);
        curatorFramework.start();

        PathChildrenCache pathChildrenCache = new PathChildrenCache(curatorFramework, path, true);

        pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                System.out.println(JSON.toJSONString(event));
            }
        });

        try {
            pathChildrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void treeCacheTest() {
        final String zkHost = "127.0.0.1";
        final String path = "/tmp";
        RetryPolicy retryPolicy = new ForeverRetryPolicy(1000, 60 * 1000);
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(zkHost, retryPolicy);
        curatorFramework.start();

        TreeCache treeCache = new TreeCache(curatorFramework, path);
        TreeCacheListener treeCacheListener = new TreeCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
                System.out.println("EVT: " + JSON.toJSONString(event));
            }
        };

        treeCache.getListenable().addListener(treeCacheListener);
        try {
            treeCache.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] agrs) {
        testRegistry();

//        treeCacheTest();
    }
}
