/*
 * Copyright 2016 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.grpc.client;

import com.example.grpc.error.EchoRequest;
import com.example.grpc.error.EchoResponse;
import com.example.grpc.error.ErrorServiceGrpc;
import com.example.grpc.server.CustomException;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by rayt on 5/16/16.
 */
public class ErrorHandlingGrpcClient {
    private static final Logger logger = Logger.getLogger(ErrorHandlingGrpcClient.class.getName());

    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080)
                .usePlaintext(true)
                .build();

        final EchoRequest request = EchoRequest.getDefaultInstance();

        ErrorServiceGrpc.ErrorServiceBlockingStub stub = ErrorServiceGrpc.newBlockingStub(channel).withInterceptors(new ClientExceptionHandlingInterceptor());

        try {
            stub.uncaughtExceptionUnaryCall(request);
        } catch (CustomException e) {
            //this is what I want: to have a client interceptor that will catch StatusRuntimeException for all stubs
            //and transform it to CustomException using information in trailers
            //prior to grpc 1.0 this was possible to do with spring AOP
            logger.log(Level.INFO, "Expected exception caught", e);
        } catch (StatusRuntimeException e) {
            //without that, I will have to catch StatusRuntimeException everywhere I call stub method
            // and check trailers every time.
            logger.log(Level.SEVERE, "Some unexpected exception!", e);
        }

        Iterator<EchoResponse> echoResponseIterator = stub.uncaughtExceptionStreamingCall(request);
        try {
            echoResponseIterator.hasNext();
        } catch (CustomException e) {
            //same here
            logger.log(Level.INFO, "Expected exception caught", e);
        } catch (StatusRuntimeException e) {
            logger.log(Level.SEVERE, "Some unexpected exception!", e);
        }

        channel.shutdown();
    }
}
