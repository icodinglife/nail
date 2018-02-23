package com.nail.core.transport.grpc;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.ByteString;
import com.nail.core.entity.RemoteMessage;
import com.nail.core.transport.ITransClient;
import com.nail.core.transport.grpc.gen.TransGrpc;
import com.nail.core.transport.grpc.gen.TransMessage;
import com.nail.core.transport.grpc.gen.TransMessageResult;
import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class GrpcTransClient implements ITransClient<Boolean> {
    private static final Logger logger = LoggerFactory.getLogger(GrpcTransClient.class);

    private TransGrpc.TransFutureStub stub;
    private Executor executor;

    public GrpcTransClient(TransGrpc.TransFutureStub stub, Executor executor) {
        this.stub = stub;
        this.executor = executor;
    }

    @Override
    public CompletableFuture<Boolean> trans(RemoteMessage.Type type, byte[] data) {
        CompletableFuture<Boolean> resultFuture = new CompletableFuture<>();

        TransMessage transMessage = TransMessage.newBuilder()
                .setType(type.getVal())
                .setContent(ByteString.copyFrom(data))
                .build();

        ListenableFuture<com.nail.core.transport.grpc.gen.TransMessageResult> listenableFuture = stub.postMessage(transMessage);

        listenableFuture.addListener(() -> {
            try {
                TransMessageResult transResult = listenableFuture.get();
                resultFuture.complete(transResult != null);
            } catch (Exception e) {
                resultFuture.completeExceptionally(e);
            }
        }, executor);

        return resultFuture;
    }

    @Override
    public void close() throws IOException {
        ((ManagedChannel) stub.getChannel()).shutdownNow();
    }
}
