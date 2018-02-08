package com.nail.core.registry.zk;

import com.alibaba.fastjson.JSON;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;

import java.io.IOException;

/**
 * Created by guofeng.qin on 2018/02/08.
 */
public class ZKTest {
    public static void main(String[] agrs) {
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
}
