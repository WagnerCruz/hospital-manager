package com.raidstack.services.exceptions;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler{

    @ExceptionHandler(TokenExpiredException.class)
    @ResponseStatus(org.springframework.http.HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public String handleTokenExpiredException(TokenExpiredException e) {
        return e.getMessage();
    }

}
