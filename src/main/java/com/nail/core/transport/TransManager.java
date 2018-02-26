package com.nail.core.transport;

import com.nail.core.NailConfig;
import com.nail.core.NailContext;
import com.nail.core.RemoteManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TransManager {
    private static final Logger logger = LoggerFactory.getLogger(TransManager.class);

    private Map<String, ITransClient> clientMap;

    private NailContext nailContext;
    private NailConfig nailConfig;
    private RemoteManager remoteManager;

    private ITransServerFactory transServerFactory;
    private ITransClientFactory transClientFactory;

    private ITransServer transServer;

    public void init(ITransClientFactory factory, ITransServerFactory transServerFactory, NailContext nailContext, NailConfig config, RemoteManager remoteManager) {
        this.transClientFactory = factory;
        this.transServerFactory = transServerFactory;
        this.nailContext = nailContext;
        this.nailConfig = config;
        this.remoteManager = remoteManager;

        clientMap = new ConcurrentHashMap<>();

        startSelfNode();
    }

    private void startSelfNode() {
        transServer = transServerFactory.buildTransServer(remoteManager);
        try {
            transServer.start(nailConfig.getPort());
        } catch (IOException e) {
            logger.error("Server Start Error.", e);
            throw new RuntimeException("Server Start Error");
        }
    }

    public ITransClient getTransClient(String host, int port) {
        String key = StringUtils.join(host, port, ':');
        ITransClient client = clientMap.get(key);
        if (client == null) {
            client = transClientFactory.buildTransClient(host, port, nailContext.getClientExecutor());
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
