package com.nail.core.transport.grpc;

import com.nail.core.transport.ITransServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrpcTransServer implements ITransServer {
    private static final Logger logger = LoggerFactory.getLogger(GrpcTransServer.class);

    public boolean start(int port) {
        // ServerBuilder.forPort(port).addService(Tns.)
        return false;
    }
}
