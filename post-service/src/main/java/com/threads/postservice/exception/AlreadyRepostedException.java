package com.threads.postservice.exception;

public class AlreadyRepostedException extends RuntimeException {
    public AlreadyRepostedException(String message) {
        super(message);
    }
}
