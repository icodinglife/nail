package com.nail.core.transport.grpc.gen;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.9.0)",
    comments = "Source: trans.proto")
public final class TransGrpc {

  private TransGrpc() {}

  public static final String SERVICE_NAME = "Trans";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getPingMethod()} instead. 
  public static final io.grpc.MethodDescriptor<com.nail.core.transport.grpc.gen.TransPingRequest,
      com.nail.core.transport.grpc.gen.TransPingResponse> METHOD_PING = getPingMethod();

  private static volatile io.grpc.MethodDescriptor<com.nail.core.transport.grpc.gen.TransPingRequest,
      com.nail.core.transport.grpc.gen.TransPingResponse> getPingMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<com.nail.core.transport.grpc.gen.TransPingRequest,
      com.nail.core.transport.grpc.gen.TransPingResponse> getPingMethod() {
    io.grpc.MethodDescriptor<com.nail.core.transport.grpc.gen.TransPingRequest, com.nail.core.transport.grpc.gen.TransPingResponse> getPingMethod;
    if ((getPingMethod = TransGrpc.getPingMethod) == null) {
      synchronized (TransGrpc.class) {
        if ((getPingMethod = TransGrpc.getPingMethod) == null) {
          TransGrpc.getPingMethod = getPingMethod = 
              io.grpc.MethodDescriptor.<com.nail.core.transport.grpc.gen.TransPingRequest, com.nail.core.transport.grpc.gen.TransPingResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "Trans", "ping"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.nail.core.transport.grpc.gen.TransPingRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.nail.core.transport.grpc.gen.TransPingResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new TransMethodDescriptorSupplier("ping"))
                  .build();
          }
        }
     }
     return getPingMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getPostMessageMethod()} instead. 
  public static final io.grpc.MethodDescriptor<com.nail.core.transport.grpc.gen.TransMessage,
      com.nail.core.transport.grpc.gen.TransMessageResult> METHOD_POST_MESSAGE = getPostMessageMethod();

  private static volatile io.grpc.MethodDescriptor<com.nail.core.transport.grpc.gen.TransMessage,
      com.nail.core.transport.grpc.gen.TransMessageResult> getPostMessageMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<com.nail.core.transport.grpc.gen.TransMessage,
      com.nail.core.transport.grpc.gen.TransMessageResult> getPostMessageMethod() {
    io.grpc.MethodDescriptor<com.nail.core.transport.grpc.gen.TransMessage, com.nail.core.transport.grpc.gen.TransMessageResult> getPostMessageMethod;
    if ((getPostMessageMethod = TransGrpc.getPostMessageMethod) == null) {
      synchronized (TransGrpc.class) {
        if ((getPostMessageMethod = TransGrpc.getPostMessageMethod) == null) {
          TransGrpc.getPostMessageMethod = getPostMessageMethod = 
              io.grpc.MethodDescriptor.<com.nail.core.transport.grpc.gen.TransMessage, com.nail.core.transport.grpc.gen.TransMessageResult>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "Trans", "postMessage"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.nail.core.transport.grpc.gen.TransMessage.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.nail.core.transport.grpc.gen.TransMessageResult.getDefaultInstance()))
                  .setSchemaDescriptor(new TransMethodDescriptorSupplier("postMessage"))
                  .build();
          }
        }
     }
     return getPostMessageMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static TransStub newStub(io.grpc.Channel channel) {
    return new TransStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static TransBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new TransBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static TransFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new TransFutureStub(channel);
  }

  /**
   */
  public static abstract class TransImplBase implements io.grpc.BindableService {

    /**
     */
    public void ping(com.nail.core.transport.grpc.gen.TransPingRequest request,
        io.grpc.stub.StreamObserver<com.nail.core.transport.grpc.gen.TransPingResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getPingMethod(), responseObserver);
    }

    /**
     */
    public void postMessage(com.nail.core.transport.grpc.gen.TransMessage request,
        io.grpc.stub.StreamObserver<com.nail.core.transport.grpc.gen.TransMessageResult> responseObserver) {
      asyncUnimplementedUnaryCall(getPostMessageMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getPingMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.nail.core.transport.grpc.gen.TransPingRequest,
                com.nail.core.transport.grpc.gen.TransPingResponse>(
                  this, METHODID_PING)))
          .addMethod(
            getPostMessageMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.nail.core.transport.grpc.gen.TransMessage,
                com.nail.core.transport.grpc.gen.TransMessageResult>(
                  this, METHODID_POST_MESSAGE)))
          .build();
    }
  }

  /**
   */
  public static final class TransStub extends io.grpc.stub.AbstractStub<TransStub> {
    private TransStub(io.grpc.Channel channel) {
      super(channel);
    }

    private TransStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TransStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new TransStub(channel, callOptions);
    }

    /**
     */
    public void ping(com.nail.core.transport.grpc.gen.TransPingRequest request,
        io.grpc.stub.StreamObserver<com.nail.core.transport.grpc.gen.TransPingResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getPingMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void postMessage(com.nail.core.transport.grpc.gen.TransMessage request,
        io.grpc.stub.StreamObserver<com.nail.core.transport.grpc.gen.TransMessageResult> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getPostMessageMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class TransBlockingStub extends io.grpc.stub.AbstractStub<TransBlockingStub> {
    private TransBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private TransBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TransBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new TransBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.nail.core.transport.grpc.gen.TransPingResponse ping(com.nail.core.transport.grpc.gen.TransPingRequest request) {
      return blockingUnaryCall(
          getChannel(), getPingMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.nail.core.transport.grpc.gen.TransMessageResult postMessage(com.nail.core.transport.grpc.gen.TransMessage request) {
      return blockingUnaryCall(
          getChannel(), getPostMessageMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class TransFutureStub extends io.grpc.stub.AbstractStub<TransFutureStub> {
    private TransFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private TransFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TransFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new TransFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.nail.core.transport.grpc.gen.TransPingResponse> ping(
        com.nail.core.transport.grpc.gen.TransPingRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getPingMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.nail.core.transport.grpc.gen.TransMessageResult> postMessage(
        com.nail.core.transport.grpc.gen.TransMessage request) {
      return futureUnaryCall(
          getChannel().newCall(getPostMessageMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_PING = 0;
  private static final int METHODID_POST_MESSAGE = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final TransImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(TransImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_PING:
          serviceImpl.ping((com.nail.core.transport.grpc.gen.TransPingRequest) request,
              (io.grpc.stub.StreamObserver<com.nail.core.transport.grpc.gen.TransPingResponse>) responseObserver);
          break;
        case METHODID_POST_MESSAGE:
          serviceImpl.postMessage((com.nail.core.transport.grpc.gen.TransMessage) request,
              (io.grpc.stub.StreamObserver<com.nail.core.transport.grpc.gen.TransMessageResult>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class TransBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    TransBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.nail.core.transport.grpc.gen.TransService.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("Trans");
    }
  }

  private static final class TransFileDescriptorSupplier
      extends TransBaseDescriptorSupplier {
    TransFileDescriptorSupplier() {}
  }

  private static final class TransMethodDescriptorSupplier
      extends TransBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    TransMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (TransGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new TransFileDescriptorSupplier())
              .addMethod(getPingMethod())
              .addMethod(getPostMessageMethod())
              .build();
        }
      }
    }
    return result;
  }
}
