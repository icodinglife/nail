package com.nail.core.transport.grpc;

import com.nail.core.RemoteManager;
import com.nail.core.transport.grpc.gen.*;
import io.grpc.stub.StreamObserver;

public class TransServerImpl extends TransGrpc.TransImplBase {
    private TransPingResponse pingResponse = TransPingResponse.newBuilder().build();

    private RemoteManager remoteManager;

    public TransServerImpl(RemoteManager remoteManager) {
        this.remoteManager = remoteManager;
    }

    @Override
    public void ping(TransPingRequest request, StreamObserver<TransPingResponse> responseObserver) {
        responseObserver.onNext(pingResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void postMessage(TransMessage request, StreamObserver<TransMessageResult> responseObserver) {
        // TODO ...
    }
}
