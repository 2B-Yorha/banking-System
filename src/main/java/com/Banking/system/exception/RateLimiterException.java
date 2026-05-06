package com.Banking.system.exception;

public class RateLimiterException extends RuntimeException{

    public RateLimiterException(String message) {
        super(message);
    }
}
