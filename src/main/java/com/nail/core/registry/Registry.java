package com.nail.core.registry;

import com.google.common.net.HostAndPort;

import java.io.Closeable;
import java.util.List;

public interface Registry extends Closeable {
    void init(List<HostAndPort> hosts);

    void register(String zone, String group, String server, String service, HostAndPort host, String extraData);

    void unregister(String zone, String group, String server, String service, HostAndPort host);
}
