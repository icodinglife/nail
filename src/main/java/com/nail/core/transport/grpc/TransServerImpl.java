package com.nail.core.transport.grpc;

import com.nail.core.RemoteManager;
import com.nail.core.entity.RemoteMessage;
import com.nail.core.transport.grpc.gen.*;
import com.nail.utils.KryoHelper;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransServerImpl extends TransGrpc.TransImplBase {
    private static final Logger logger = LoggerFactory.getLogger(TransServerImpl.class);

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
        int v = request.getType();
        RemoteMessage.Type type = RemoteMessage.Type.fromVal(v);
        switch (type) {
            case REQUEST:
                remoteManager.recvRequest(KryoHelper.readClassAndObject(request.getContent().toByteArray()));
                break;
            case RESPONSE:
                remoteManager.recvResponse(KryoHelper.readClassAndObject(request.getContent().toByteArray()));
                break;
            case UNKNOWN:
                logger.error("Unknown Message Type.");
                break;
        }
    }
}
