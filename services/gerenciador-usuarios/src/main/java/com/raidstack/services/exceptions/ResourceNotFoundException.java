package com.raidstack.services.exceptions;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends ResourceExceptionDefault {

    public ResourceNotFoundException(String message) {
        super(message);
        this.titulo = "Não localizado informação";
        this.httpStatus = HttpStatus.NOT_FOUND;
    }

}
