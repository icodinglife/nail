package com.nail.core.transport;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TransManager {
    private static final Logger logger = LoggerFactory.getLogger(TransManager.class);

    private Map<String, ITransClient> clientMap;
    private ITransClientFactory transClientFactory;

    public void init(ITransClientFactory factory) {
        this.transClientFactory = factory;
        clientMap = new ConcurrentHashMap<>();
    }

    public ITransClient getTransClient(String host, int port) {
        String key = StringUtils.join(host, port, ':');
        ITransClient client = clientMap.get(key);
        if (client == null) {
            client = transClientFactory.buildTransClient(host, port);
            ITransClient oldClient = clientMap.putIfAbsent(key, client);
            if (oldClient != null) {
                client = oldClient;
            }
        }
        return client;
    }

    public void removeTransClient(String host, int port) {
        String key = StringUtils.join(host, port, ':');
        ITransClient client = clientMap.remove(key);
        if (client != null) {
            try {
                client.close();
            } catch (Exception e) {
                logger.error(key + " TransClient Close Error.", e);
            }
        }
    }
}
