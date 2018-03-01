package com.example.grpc.server;

/**
 * Created by rayt on 6/24/17.
 */
public class CustomException extends RuntimeException {
    public CustomException() {
    }

    public CustomException(String message) {
        super(message);
    }

    public String getCustomErrorCode() {
        return "CUSTOM_ERROR_CODE";
    }
}
