package com.raidstack.services.exceptions;

import org.springframework.http.HttpStatus;

import java.util.List;

public class ResourceConflictException extends ResourceExceptionDefault {

    public ResourceConflictException(String message) {
        super(message);
        this.titulo = "Conflito de Dados";
        this.httpStatus = HttpStatus.CONFLICT;
    }

    public ResourceConflictException(String message, List<String> errors) {
        this(message);
        this.errors = errors;
    }
}
