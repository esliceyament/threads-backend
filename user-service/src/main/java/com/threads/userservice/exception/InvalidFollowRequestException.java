package com.threads.userservice.exception;

public class InvalidFollowRequestException extends RuntimeException {
    public InvalidFollowRequestException(String message) {
        super(message);
    }
}
