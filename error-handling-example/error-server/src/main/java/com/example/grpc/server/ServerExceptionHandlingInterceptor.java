package com.example.grpc.server;

import io.grpc.ForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

public class ServerExceptionHandlingInterceptor implements ServerInterceptor {
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {
        return new Listener<>(serverCallHandler.startCall(serverCall, metadata));
    }

    private static final class Listener<ReqT> extends ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT> {
        protected Listener(ServerCall.Listener<ReqT> delegate) {
            super(delegate);
        }

        @Override
        public void onHalfClose() {
            try {
                super.onHalfClose();
            } catch (RuntimeException e) {
                if (e instanceof CustomException) {
                    Metadata trailers = new Metadata();
                    trailers.put(Metadata.Key.of("customErrorCode", Metadata.ASCII_STRING_MARSHALLER), ((CustomException) e).getCustomErrorCode());
                    throw new StatusRuntimeException(Status.INTERNAL.withDescription(e.getMessage()), trailers);
                }

                throw e;
            }
        }
    }
}