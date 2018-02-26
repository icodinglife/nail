package com.nail.core.transport.grpc;

import com.nail.core.transport.ITransClient;
import com.nail.core.transport.ITransClientFactory;
import com.nail.core.transport.grpc.gen.TransGrpc;
import io.grpc.Channel;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.channel.EventLoopGroup;

import java.util.concurrent.Executor;

public class GrpcClientFactory implements ITransClientFactory {
    @Override
    public ITransClient buildTransClient(String host, int port, Executor executor) {
        Channel channel = NettyChannelBuilder
                .forAddress(host, port)
                .usePlaintext(true)
                .eventLoopGroup((EventLoopGroup) executor)
                .build();
        TransGrpc.TransFutureStub stub = TransGrpc.newFutureStub(channel);
        return new GrpcTransClient(stub, executor);
    }
}
