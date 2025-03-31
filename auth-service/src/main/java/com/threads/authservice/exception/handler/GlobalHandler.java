package com.threads.authservice.exception.handler;

import com.threads.authservice.dto.ExceptionDto;
import com.threads.authservice.exception.AlreadyExistsException;
import com.threads.authservice.exception.InvalidCredentialsException;
import com.threads.authservice.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalHandler {

    @ExceptionHandler(AlreadyExistsException.class)
    @ResponseStatus(HttpStatus.ALREADY_REPORTED)
    public ExceptionDto handleAlreadyExists(AlreadyExistsException e) {
        return new ExceptionDto(HttpStatus.ALREADY_REPORTED.value(), e.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ExceptionDto handleInvalidCredentials(InvalidCredentialsException e) {
        return new ExceptionDto(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDto handleNotFound(NotFoundException e) {
        return new ExceptionDto(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }
}
