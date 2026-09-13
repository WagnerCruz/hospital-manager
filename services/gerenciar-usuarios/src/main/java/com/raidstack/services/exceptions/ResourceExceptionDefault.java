package com.raidstack.services.exceptions;

import org.springframework.http.HttpStatus;

import java.util.List;

public class ResourceExceptionDefault extends RuntimeException {

    protected String titulo;
    protected HttpStatus httpStatus;
    protected List<String> errors;

    public ResourceExceptionDefault(String message) {
        super(message);
        this.titulo = "Erro interno do sistema";
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        this.errors = null;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getTitulo() {
        return titulo;
    }

    public List<String> getErrors() {
        return errors;
    }
}
