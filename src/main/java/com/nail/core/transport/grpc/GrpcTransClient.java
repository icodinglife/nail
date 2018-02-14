package com.nail.core.transport.grpc;

import com.nail.core.entity.RemoteMessage;
import com.nail.core.transport.ITransClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GrpcTransClient implements ITransClient {
    private static final Logger logger = LoggerFactory.getLogger(GrpcTransClient.class);

    @Override
    public boolean trans(RemoteMessage.Type type, byte[] data) {
        return false;
    }

    @Override
    public void close() throws IOException {

    }
}
