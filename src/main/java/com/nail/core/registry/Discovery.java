package com.nail.core.registry;

import com.google.common.net.HostAndPort;

import java.io.Closeable;
import java.util.List;

/**
 * Created by guofeng.qin on 2018/02/08.
 */
public interface Discovery extends Closeable {
    void init(List<HostAndPort> zkHosts);

    void addListener(String path, DiscoveryListener listener);
}
