package com.threads.postservice.exception.handler;

import com.threads.postservice.dto.ExceptionDto;
import com.threads.postservice.exception.AlreadyRepostedException;
import com.threads.postservice.exception.NotFoundException;
import com.threads.postservice.exception.NotPinnedReplyException;
import com.threads.postservice.exception.OwnershipException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDto notFoundHandler(NotFoundException notFoundException) {
        return new ExceptionDto(HttpStatus.NOT_FOUND.value(), notFoundException.getMessage());
    }

    @ExceptionHandler(OwnershipException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ExceptionDto ownershipHandler(OwnershipException ownershipException) {
        return new ExceptionDto(HttpStatus.FORBIDDEN.value(), ownershipException.getMessage());
    }

    @ExceptionHandler(NotPinnedReplyException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionDto notPinnedReplyHandler(NotPinnedReplyException notPinnedReplyException) {
        return new ExceptionDto(HttpStatus.CONTINUE.value(), notPinnedReplyException.getMessage());
    }

    @ExceptionHandler(AlreadyRepostedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto alreadyRepostedException(AlreadyRepostedException alreadyRepostedException) {
        return new ExceptionDto(HttpStatus.BAD_REQUEST.value(), alreadyRepostedException.getMessage());
    }

}
