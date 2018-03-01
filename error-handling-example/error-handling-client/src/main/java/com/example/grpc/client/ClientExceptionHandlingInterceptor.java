package com.example.grpc.client;

import com.example.grpc.server.CustomException;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.ForwardingClientCallListener;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Status;

public class ClientExceptionHandlingInterceptor implements ClientInterceptor {
    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> methodDescriptor, CallOptions callOptions, Channel channel) {
        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(channel.newCall(methodDescriptor, callOptions)) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                super.start(new ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT>(responseListener) {
                    @Override
                    public void onClose(Status status, Metadata trailers) {
                        if ("CUSTOM_ERROR_CODE".equals(trailers.get(Metadata.Key.of("customErrorCode", Metadata.ASCII_STRING_MARSHALLER)))) {
                            //throwing out of this method is not an option
                            //exception is simply logged and ignored
//                            throw new CustomException(status.getDescription());
                        }

                        //this is call is required
                        //Status is final call, and I cannot override Status#asRuntimeException (I would have to extend CustomException from StatusRuntimeException if that was possible)
                        super.onClose(status, trailers);
                    }
                }, headers);
            }
        };
    }
}