package com.nail.core.transport.grpc;

import com.nail.core.RemoteManager;
import com.nail.core.transport.ITransServer;
import io.grpc.Server;
import io.grpc.netty.NettyServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GrpcTransServer implements ITransServer {
    private static final Logger logger = LoggerFactory.getLogger(GrpcTransServer.class);

    private RemoteManager remoteManager;

    private Server server;
    private TransServerImpl serverImpl;

    public GrpcTransServer(RemoteManager remoteManager) {
        this.remoteManager = remoteManager;

        init();
    }

    public void init() {
        serverImpl = new TransServerImpl(remoteManager);
    }

    @Override
    public boolean start(int port) throws IOException {
        server = NettyServerBuilder.forPort(port).addService(serverImpl).build();
        server.start();
        return true;
    }

    @Override
    public void close() throws IOException {
        if (server != null) {
            server.shutdownNow();
        }
    }
}
