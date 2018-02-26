package com.nail.core.transport.grpc;

import com.nail.core.RemoteManager;
import com.nail.core.transport.ITransServer;
import com.nail.core.transport.ITransServerFactory;

/**
 * Created by guofeng.qin on 2018/02/24.
 */
public class GrpcServerFactory implements ITransServerFactory {

    @Override
    public ITransServer buildTransServer(RemoteManager remoteManager) {
        return new GrpcTransServer(remoteManager);
    }
}
