package br.ufu.facom.gbc074.kvs;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.39.0)",
    comments = "Source: kvs.proto")
public final class KVSGrpc {

  private KVSGrpc() {}

  public static final String SERVICE_NAME = "kvs.KVS";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<br.ufu.facom.gbc074.kvs.ChaveValor,
      br.ufu.facom.gbc074.kvs.Versao> getInsereMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Insere",
      requestType = br.ufu.facom.gbc074.kvs.ChaveValor.class,
      responseType = br.ufu.facom.gbc074.kvs.Versao.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<br.ufu.facom.gbc074.kvs.ChaveValor,
      br.ufu.facom.gbc074.kvs.Versao> getInsereMethod() {
    io.grpc.MethodDescriptor<br.ufu.facom.gbc074.kvs.ChaveValor, br.ufu.facom.gbc074.kvs.Versao> getInsereMethod;
    if ((getInsereMethod = KVSGrpc.getInsereMethod) == null) {
      synchronized (KVSGrpc.class) {
        if ((getInsereMethod = KVSGrpc.getInsereMethod) == null) {
          KVSGrpc.getInsereMethod = getInsereMethod =
              io.grpc.MethodDescriptor.<br.ufu.facom.gbc074.kvs.ChaveValor, br.ufu.facom.gbc074.kvs.Versao>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Insere"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  br.ufu.facom.gbc074.kvs.ChaveValor.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  br.ufu.facom.gbc074.kvs.Versao.getDefaultInstance()))
              .setSchemaDescriptor(new KVSMethodDescriptorSupplier("Insere"))
              .build();
        }
      }
    }
    return getInsereMethod;
  }

  private static volatile io.grpc.MethodDescriptor<br.ufu.facom.gbc074.kvs.ChaveVersao,
      br.ufu.facom.gbc074.kvs.Tupla> getConsultaMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Consulta",
      requestType = br.ufu.facom.gbc074.kvs.ChaveVersao.class,
      responseType = br.ufu.facom.gbc074.kvs.Tupla.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<br.ufu.facom.gbc074.kvs.ChaveVersao,
      br.ufu.facom.gbc074.kvs.Tupla> getConsultaMethod() {
    io.grpc.MethodDescriptor<br.ufu.facom.gbc074.kvs.ChaveVersao, br.ufu.facom.gbc074.kvs.Tupla> getConsultaMethod;
    if ((getConsultaMethod = KVSGrpc.getConsultaMethod) == null) {
      synchronized (KVSGrpc.class) {
        if ((getConsultaMethod = KVSGrpc.getConsultaMethod) == null) {
          KVSGrpc.getConsultaMethod = getConsultaMethod =
              io.grpc.MethodDescriptor.<br.ufu.facom.gbc074.kvs.ChaveVersao, br.ufu.facom.gbc074.kvs.Tupla>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Consulta"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  br.ufu.facom.gbc074.kvs.ChaveVersao.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  br.ufu.facom.gbc074.kvs.Tupla.getDefaultInstance()))
              .setSchemaDescriptor(new KVSMethodDescriptorSupplier("Consulta"))
              .build();
        }
      }
    }
    return getConsultaMethod;
  }

  private static volatile io.grpc.MethodDescriptor<br.ufu.facom.gbc074.kvs.ChaveVersao,
      br.ufu.facom.gbc074.kvs.Versao> getRemoveMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Remove",
      requestType = br.ufu.facom.gbc074.kvs.ChaveVersao.class,
      responseType = br.ufu.facom.gbc074.kvs.Versao.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<br.ufu.facom.gbc074.kvs.ChaveVersao,
      br.ufu.facom.gbc074.kvs.Versao> getRemoveMethod() {
    io.grpc.MethodDescriptor<br.ufu.facom.gbc074.kvs.ChaveVersao, br.ufu.facom.gbc074.kvs.Versao> getRemoveMethod;
    if ((getRemoveMethod = KVSGrpc.getRemoveMethod) == null) {
      synchronized (KVSGrpc.class) {
        if ((getRemoveMethod = KVSGrpc.getRemoveMethod) == null) {
          KVSGrpc.getRemoveMethod = getRemoveMethod =
              io.grpc.MethodDescriptor.<br.ufu.facom.gbc074.kvs.ChaveVersao, br.ufu.facom.gbc074.kvs.Versao>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Remove"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  br.ufu.facom.gbc074.kvs.ChaveVersao.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  br.ufu.facom.gbc074.kvs.Versao.getDefaultInstance()))
              .setSchemaDescriptor(new KVSMethodDescriptorSupplier("Remove"))
              .build();
        }
      }
    }
    return getRemoveMethod;
  }

  private static volatile io.grpc.MethodDescriptor<br.ufu.facom.gbc074.kvs.ChaveValor,
      br.ufu.facom.gbc074.kvs.Versao> getInsereVariasMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "InsereVarias",
      requestType = br.ufu.facom.gbc074.kvs.ChaveValor.class,
      responseType = br.ufu.facom.gbc074.kvs.Versao.class,
      methodType = io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
  public static io.grpc.MethodDescriptor<br.ufu.facom.gbc074.kvs.ChaveValor,
      br.ufu.facom.gbc074.kvs.Versao> getInsereVariasMethod() {
    io.grpc.MethodDescriptor<br.ufu.facom.gbc074.kvs.ChaveValor, br.ufu.facom.gbc074.kvs.Versao> getInsereVariasMethod;
    if ((getInsereVariasMethod = KVSGrpc.getInsereVariasMethod) == null) {
      synchronized (KVSGrpc.class) {
        if ((getInsereVariasMethod = KVSGrpc.getInsereVariasMethod) == null) {
          KVSGrpc.getInsereVariasMethod = getInsereVariasMethod =
              io.grpc.MethodDescriptor.<br.ufu.facom.gbc074.kvs.ChaveValor, br.ufu.facom.gbc074.kvs.Versao>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "InsereVarias"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  br.ufu.facom.gbc074.kvs.ChaveValor.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  br.ufu.facom.gbc074.kvs.Versao.getDefaultInstance()))
              .setSchemaDescriptor(new KVSMethodDescriptorSupplier("InsereVarias"))
              .build();
        }
      }
    }
    return getInsereVariasMethod;
  }

  private static volatile io.grpc.MethodDescriptor<br.ufu.facom.gbc074.kvs.ChaveVersao,
      br.ufu.facom.gbc074.kvs.Tupla> getConsultaVariasMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ConsultaVarias",
      requestType = br.ufu.facom.gbc074.kvs.ChaveVersao.class,
      responseType = br.ufu.facom.gbc074.kvs.Tupla.class,
      methodType = io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
  public static io.grpc.MethodDescriptor<br.ufu.facom.gbc074.kvs.ChaveVersao,
      br.ufu.facom.gbc074.kvs.Tupla> getConsultaVariasMethod() {
    io.grpc.MethodDescriptor<br.ufu.facom.gbc074.kvs.ChaveVersao, br.ufu.facom.gbc074.kvs.Tupla> getConsultaVariasMethod;
    if ((getConsultaVariasMethod = KVSGrpc.getConsultaVariasMethod) == null) {
      synchronized (KVSGrpc.class) {
        if ((getConsultaVariasMethod = KVSGrpc.getConsultaVariasMethod) == null) {
          KVSGrpc.getConsultaVariasMethod = getConsultaVariasMethod =
              io.grpc.MethodDescriptor.<br.ufu.facom.gbc074.kvs.ChaveVersao, br.ufu.facom.gbc074.kvs.Tupla>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ConsultaVarias"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  br.ufu.facom.gbc074.kvs.ChaveVersao.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  br.ufu.facom.gbc074.kvs.Tupla.getDefaultInstance()))
              .setSchemaDescriptor(new KVSMethodDescriptorSupplier("ConsultaVarias"))
              .build();
        }
      }
    }
    return getConsultaVariasMethod;
  }

  private static volatile io.grpc.MethodDescriptor<br.ufu.facom.gbc074.kvs.ChaveVersao,
      br.ufu.facom.gbc074.kvs.Versao> getRemoveVariasMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RemoveVarias",
      requestType = br.ufu.facom.gbc074.kvs.ChaveVersao.class,
      responseType = br.ufu.facom.gbc074.kvs.Versao.class,
      methodType = io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
  public static io.grpc.MethodDescriptor<br.ufu.facom.gbc074.kvs.ChaveVersao,
      br.ufu.facom.gbc074.kvs.Versao> getRemoveVariasMethod() {
    io.grpc.MethodDescriptor<br.ufu.facom.gbc074.kvs.ChaveVersao, br.ufu.facom.gbc074.kvs.Versao> getRemoveVariasMethod;
    if ((getRemoveVariasMethod = KVSGrpc.getRemoveVariasMethod) == null) {
      synchronized (KVSGrpc.class) {
        if ((getRemoveVariasMethod = KVSGrpc.getRemoveVariasMethod) == null) {
          KVSGrpc.getRemoveVariasMethod = getRemoveVariasMethod =
              io.grpc.MethodDescriptor.<br.ufu.facom.gbc074.kvs.ChaveVersao, br.ufu.facom.gbc074.kvs.Versao>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "RemoveVarias"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  br.ufu.facom.gbc074.kvs.ChaveVersao.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  br.ufu.facom.gbc074.kvs.Versao.getDefaultInstance()))
              .setSchemaDescriptor(new KVSMethodDescriptorSupplier("RemoveVarias"))
              .build();
        }
      }
    }
    return getRemoveVariasMethod;
  }

  private static volatile io.grpc.MethodDescriptor<br.ufu.facom.gbc074.kvs.Versao,
      br.ufu.facom.gbc074.kvs.Tupla> getSnapshotMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Snapshot",
      requestType = br.ufu.facom.gbc074.kvs.Versao.class,
      responseType = br.ufu.facom.gbc074.kvs.Tupla.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<br.ufu.facom.gbc074.kvs.Versao,
      br.ufu.facom.gbc074.kvs.Tupla> getSnapshotMethod() {
    io.grpc.MethodDescriptor<br.ufu.facom.gbc074.kvs.Versao, br.ufu.facom.gbc074.kvs.Tupla> getSnapshotMethod;
    if ((getSnapshotMethod = KVSGrpc.getSnapshotMethod) == null) {
      synchronized (KVSGrpc.class) {
        if ((getSnapshotMethod = KVSGrpc.getSnapshotMethod) == null) {
          KVSGrpc.getSnapshotMethod = getSnapshotMethod =
              io.grpc.MethodDescriptor.<br.ufu.facom.gbc074.kvs.Versao, br.ufu.facom.gbc074.kvs.Tupla>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Snapshot"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  br.ufu.facom.gbc074.kvs.Versao.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  br.ufu.facom.gbc074.kvs.Tupla.getDefaultInstance()))
              .setSchemaDescriptor(new KVSMethodDescriptorSupplier("Snapshot"))
              .build();
        }
      }
    }
    return getSnapshotMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static KVSStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<KVSStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<KVSStub>() {
        @java.lang.Override
        public KVSStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new KVSStub(channel, callOptions);
        }
      };
    return KVSStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static KVSBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<KVSBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<KVSBlockingStub>() {
        @java.lang.Override
        public KVSBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new KVSBlockingStub(channel, callOptions);
        }
      };
    return KVSBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static KVSFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<KVSFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<KVSFutureStub>() {
        @java.lang.Override
        public KVSFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new KVSFutureStub(channel, callOptions);
        }
      };
    return KVSFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class KVSImplBase implements io.grpc.BindableService {

    /**
     */
    public void insere(br.ufu.facom.gbc074.kvs.ChaveValor request,
        io.grpc.stub.StreamObserver<br.ufu.facom.gbc074.kvs.Versao> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getInsereMethod(), responseObserver);
    }

    /**
     */
    public void consulta(br.ufu.facom.gbc074.kvs.ChaveVersao request,
        io.grpc.stub.StreamObserver<br.ufu.facom.gbc074.kvs.Tupla> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getConsultaMethod(), responseObserver);
    }

    /**
     */
    public void remove(br.ufu.facom.gbc074.kvs.ChaveVersao request,
        io.grpc.stub.StreamObserver<br.ufu.facom.gbc074.kvs.Versao> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getRemoveMethod(), responseObserver);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<br.ufu.facom.gbc074.kvs.ChaveValor> insereVarias(
        io.grpc.stub.StreamObserver<br.ufu.facom.gbc074.kvs.Versao> responseObserver) {
      return io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall(getInsereVariasMethod(), responseObserver);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<br.ufu.facom.gbc074.kvs.ChaveVersao> consultaVarias(
        io.grpc.stub.StreamObserver<br.ufu.facom.gbc074.kvs.Tupla> responseObserver) {
      return io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall(getConsultaVariasMethod(), responseObserver);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<br.ufu.facom.gbc074.kvs.ChaveVersao> removeVarias(
        io.grpc.stub.StreamObserver<br.ufu.facom.gbc074.kvs.Versao> responseObserver) {
      return io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall(getRemoveVariasMethod(), responseObserver);
    }

    /**
     */
    public void snapshot(br.ufu.facom.gbc074.kvs.Versao request,
        io.grpc.stub.StreamObserver<br.ufu.facom.gbc074.kvs.Tupla> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSnapshotMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getInsereMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                br.ufu.facom.gbc074.kvs.ChaveValor,
                br.ufu.facom.gbc074.kvs.Versao>(
                  this, METHODID_INSERE)))
          .addMethod(
            getConsultaMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                br.ufu.facom.gbc074.kvs.ChaveVersao,
                br.ufu.facom.gbc074.kvs.Tupla>(
                  this, METHODID_CONSULTA)))
          .addMethod(
            getRemoveMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                br.ufu.facom.gbc074.kvs.ChaveVersao,
                br.ufu.facom.gbc074.kvs.Versao>(
                  this, METHODID_REMOVE)))
          .addMethod(
            getInsereVariasMethod(),
            io.grpc.stub.ServerCalls.asyncBidiStreamingCall(
              new MethodHandlers<
                br.ufu.facom.gbc074.kvs.ChaveValor,
                br.ufu.facom.gbc074.kvs.Versao>(
                  this, METHODID_INSERE_VARIAS)))
          .addMethod(
            getConsultaVariasMethod(),
            io.grpc.stub.ServerCalls.asyncBidiStreamingCall(
              new MethodHandlers<
                br.ufu.facom.gbc074.kvs.ChaveVersao,
                br.ufu.facom.gbc074.kvs.Tupla>(
                  this, METHODID_CONSULTA_VARIAS)))
          .addMethod(
            getRemoveVariasMethod(),
            io.grpc.stub.ServerCalls.asyncBidiStreamingCall(
              new MethodHandlers<
                br.ufu.facom.gbc074.kvs.ChaveVersao,
                br.ufu.facom.gbc074.kvs.Versao>(
                  this, METHODID_REMOVE_VARIAS)))
          .addMethod(
            getSnapshotMethod(),
            io.grpc.stub.ServerCalls.asyncServerStreamingCall(
              new MethodHandlers<
                br.ufu.facom.gbc074.kvs.Versao,
                br.ufu.facom.gbc074.kvs.Tupla>(
                  this, METHODID_SNAPSHOT)))
          .build();
    }
  }

  /**
   */
  public static final class KVSStub extends io.grpc.stub.AbstractAsyncStub<KVSStub> {
    private KVSStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected KVSStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new KVSStub(channel, callOptions);
    }

    /**
     */
    public void insere(br.ufu.facom.gbc074.kvs.ChaveValor request,
        io.grpc.stub.StreamObserver<br.ufu.facom.gbc074.kvs.Versao> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getInsereMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void consulta(br.ufu.facom.gbc074.kvs.ChaveVersao request,
        io.grpc.stub.StreamObserver<br.ufu.facom.gbc074.kvs.Tupla> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getConsultaMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void remove(br.ufu.facom.gbc074.kvs.ChaveVersao request,
        io.grpc.stub.StreamObserver<br.ufu.facom.gbc074.kvs.Versao> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getRemoveMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<br.ufu.facom.gbc074.kvs.ChaveValor> insereVarias(
        io.grpc.stub.StreamObserver<br.ufu.facom.gbc074.kvs.Versao> responseObserver) {
      return io.grpc.stub.ClientCalls.asyncBidiStreamingCall(
          getChannel().newCall(getInsereVariasMethod(), getCallOptions()), responseObserver);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<br.ufu.facom.gbc074.kvs.ChaveVersao> consultaVarias(
        io.grpc.stub.StreamObserver<br.ufu.facom.gbc074.kvs.Tupla> responseObserver) {
      return io.grpc.stub.ClientCalls.asyncBidiStreamingCall(
          getChannel().newCall(getConsultaVariasMethod(), getCallOptions()), responseObserver);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<br.ufu.facom.gbc074.kvs.ChaveVersao> removeVarias(
        io.grpc.stub.StreamObserver<br.ufu.facom.gbc074.kvs.Versao> responseObserver) {
      return io.grpc.stub.ClientCalls.asyncBidiStreamingCall(
          getChannel().newCall(getRemoveVariasMethod(), getCallOptions()), responseObserver);
    }

    /**
     */
    public void snapshot(br.ufu.facom.gbc074.kvs.Versao request,
        io.grpc.stub.StreamObserver<br.ufu.facom.gbc074.kvs.Tupla> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getSnapshotMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class KVSBlockingStub extends io.grpc.stub.AbstractBlockingStub<KVSBlockingStub> {
    private KVSBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected KVSBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new KVSBlockingStub(channel, callOptions);
    }

    /**
     */
    public br.ufu.facom.gbc074.kvs.Versao insere(br.ufu.facom.gbc074.kvs.ChaveValor request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getInsereMethod(), getCallOptions(), request);
    }

    /**
     */
    public br.ufu.facom.gbc074.kvs.Tupla consulta(br.ufu.facom.gbc074.kvs.ChaveVersao request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getConsultaMethod(), getCallOptions(), request);
    }

    /**
     */
    public br.ufu.facom.gbc074.kvs.Versao remove(br.ufu.facom.gbc074.kvs.ChaveVersao request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getRemoveMethod(), getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<br.ufu.facom.gbc074.kvs.Tupla> snapshot(
        br.ufu.facom.gbc074.kvs.Versao request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getSnapshotMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class KVSFutureStub extends io.grpc.stub.AbstractFutureStub<KVSFutureStub> {
    private KVSFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected KVSFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new KVSFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<br.ufu.facom.gbc074.kvs.Versao> insere(
        br.ufu.facom.gbc074.kvs.ChaveValor request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getInsereMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<br.ufu.facom.gbc074.kvs.Tupla> consulta(
        br.ufu.facom.gbc074.kvs.ChaveVersao request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getConsultaMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<br.ufu.facom.gbc074.kvs.Versao> remove(
        br.ufu.facom.gbc074.kvs.ChaveVersao request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getRemoveMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_INSERE = 0;
  private static final int METHODID_CONSULTA = 1;
  private static final int METHODID_REMOVE = 2;
  private static final int METHODID_SNAPSHOT = 3;
  private static final int METHODID_INSERE_VARIAS = 4;
  private static final int METHODID_CONSULTA_VARIAS = 5;
  private static final int METHODID_REMOVE_VARIAS = 6;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final KVSImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(KVSImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_INSERE:
          serviceImpl.insere((br.ufu.facom.gbc074.kvs.ChaveValor) request,
              (io.grpc.stub.StreamObserver<br.ufu.facom.gbc074.kvs.Versao>) responseObserver);
          break;
        case METHODID_CONSULTA:
          serviceImpl.consulta((br.ufu.facom.gbc074.kvs.ChaveVersao) request,
              (io.grpc.stub.StreamObserver<br.ufu.facom.gbc074.kvs.Tupla>) responseObserver);
          break;
        case METHODID_REMOVE:
          serviceImpl.remove((br.ufu.facom.gbc074.kvs.ChaveVersao) request,
              (io.grpc.stub.StreamObserver<br.ufu.facom.gbc074.kvs.Versao>) responseObserver);
          break;
        case METHODID_SNAPSHOT:
          serviceImpl.snapshot((br.ufu.facom.gbc074.kvs.Versao) request,
              (io.grpc.stub.StreamObserver<br.ufu.facom.gbc074.kvs.Tupla>) responseObserver);
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
        case METHODID_INSERE_VARIAS:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.insereVarias(
              (io.grpc.stub.StreamObserver<br.ufu.facom.gbc074.kvs.Versao>) responseObserver);
        case METHODID_CONSULTA_VARIAS:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.consultaVarias(
              (io.grpc.stub.StreamObserver<br.ufu.facom.gbc074.kvs.Tupla>) responseObserver);
        case METHODID_REMOVE_VARIAS:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.removeVarias(
              (io.grpc.stub.StreamObserver<br.ufu.facom.gbc074.kvs.Versao>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class KVSBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    KVSBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return br.ufu.facom.gbc074.kvs.Kvs.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("KVS");
    }
  }

  private static final class KVSFileDescriptorSupplier
      extends KVSBaseDescriptorSupplier {
    KVSFileDescriptorSupplier() {}
  }

  private static final class KVSMethodDescriptorSupplier
      extends KVSBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    KVSMethodDescriptorSupplier(String methodName) {
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
      synchronized (KVSGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new KVSFileDescriptorSupplier())
              .addMethod(getInsereMethod())
              .addMethod(getConsultaMethod())
              .addMethod(getRemoveMethod())
              .addMethod(getInsereVariasMethod())
              .addMethod(getConsultaVariasMethod())
              .addMethod(getRemoveVariasMethod())
              .addMethod(getSnapshotMethod())
              .build();
        }
      }
    }
    return result;
  }
}
