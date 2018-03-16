package com.nail.core;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.FiberUtil;
import co.paralleluniverse.strands.SuspendableCallable;
import com.nail.core.loadbalance.LoadbalanceManager;
import com.nail.core.registry.Registry;
import com.nail.core.registry.ServiceDiscovery;
import com.nail.core.registry.zk.ZKRegistry;
import com.nail.core.transport.ITransClientFactory;
import com.nail.core.transport.ITransServerFactory;
import com.nail.core.transport.TransManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

/**
 * Created by guofeng.qin on 2018/02/24.
 */
public class NailApp {
    private static final Logger logger = LoggerFactory.getLogger(NailApp.class);

    private NailConfig nailConfig;
    private ITransServerFactory serverFactory;
    private ITransClientFactory clientFactory;

    private NailContext nailContext;
    private TransManager transManager;
    private Registry registry;
    private ServiceDiscovery serviceDiscovery;
    private LoadbalanceManager loadbalanceManager;
    private ServiceManager serviceManager;
    private RemoteManager remoteManager;

    public NailApp(NailConfig nailConfig, ITransServerFactory serverFactory, ITransClientFactory clientFactory) {
        this.nailConfig = nailConfig;
        this.serverFactory = serverFactory;
        this.clientFactory = clientFactory;
    }

    public <V> V init(SuspendableCallable<V> run) throws ExecutionException, InterruptedException {
        nailContext = new NailContext();
        nailContext.init(nailConfig);

        transManager = new TransManager();
        remoteManager = new RemoteManager();

        transManager.init(clientFactory, serverFactory, nailContext, nailConfig, remoteManager);

        registry = new ZKRegistry();
        registry.init(nailConfig.getZKHosts());

        serviceDiscovery = new ServiceDiscovery();
        serviceDiscovery.init(registry, transManager, nailConfig.getZone(), nailConfig.getName(), nailConfig.getHost(), nailConfig.getPort());

        loadbalanceManager = new LoadbalanceManager();
        loadbalanceManager.init();

        serviceManager = new ServiceManager();
        serviceManager.init(nailContext, nailConfig, serviceDiscovery);

        remoteManager.init(nailContext, serviceDiscovery, loadbalanceManager, transManager, serviceManager);

        return FiberUtil.runInFiber(nailContext.getFiberScheduler(), run);
    }

    public NailConfig getNailConfig() {
        return nailConfig;
    }

    public NailContext getNailContext() {
        return nailContext;
    }

    public TransManager getTransManager() {
        return transManager;
    }

    public ServiceDiscovery getServiceDiscovery() {
        return serviceDiscovery;
    }

    public LoadbalanceManager getLoadbalanceManager() {
        return loadbalanceManager;
    }

    public ServiceManager getServiceManager() {
        return serviceManager;
    }

    public RemoteManager getRemoteManager() {
        return remoteManager;
    }
}
