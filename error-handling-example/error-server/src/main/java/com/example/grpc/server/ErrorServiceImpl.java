package com.example.grpc.server;

import com.example.grpc.error.EchoRequest;
import com.example.grpc.error.EchoResponse;
import com.example.grpc.error.ErrorServiceGrpc;
import io.grpc.stub.StreamObserver;

/**
 * Created by rayt on 6/24/17.
 */
public class ErrorServiceImpl extends ErrorServiceGrpc.ErrorServiceImplBase {

    @Override
    public void uncaughtExceptionUnaryCall(EchoRequest request, StreamObserver<EchoResponse> responseObserver) {
        throw new CustomException("uncaughtExceptionUnaryCall()");
    }

    @Override
    public void uncaughtExceptionStreamingCall(EchoRequest request, StreamObserver<EchoResponse> responseObserver) {
        throw new CustomException("uncaughtExceptionStreamingCall()");
    }
}
