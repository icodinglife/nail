package com.nail.core.registry;

import com.google.common.net.HostAndPort;

import java.io.Closeable;
import java.util.List;

public interface Registry extends Closeable {
    void init(List<HostAndPort> hosts);

    boolean register(String namespace, String zone, String group, String server, String service, HostAndPort host, byte[] registryData);

    boolean unregister(String namespace, String zone, String group, String server, String service, HostAndPort host);

    void addTreeListener(String path, DiscoveryListener listener);
}
