package com.threads.userservice.exception.handler;

import com.threads.userservice.dto.ExceptionDto;
import com.threads.userservice.exception.*;
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

    @ExceptionHandler(AlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionDto alreadyExistsHandler(AlreadyExistsException alreadyExistsException) {
        return new ExceptionDto(HttpStatus.CONFLICT.value(), alreadyExistsException.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ExceptionDto accessDeniedHandler(AccessDeniedException accessDeniedException) {
        return new ExceptionDto(HttpStatus.FORBIDDEN.value(), accessDeniedException.getMessage());
    }

    @ExceptionHandler(InvalidFollowRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto invalidFollowRequestException(InvalidFollowRequestException invalidFollowRequestException) {
        return new ExceptionDto(HttpStatus.BAD_REQUEST.value(), invalidFollowRequestException.getMessage());
    }

    @ExceptionHandler(AlreadyFollowingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto alreadyFollowingHandler(AlreadyFollowingException alreadyFollowingException) {
        return new ExceptionDto(HttpStatus.BAD_REQUEST.value(), alreadyFollowingException.getMessage());
    }


}
