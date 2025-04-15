package com.threads.postservice.exception;

public class NotPinnedReplyException extends RuntimeException {
    public NotPinnedReplyException(String message) {
        super(message);
    }
}
