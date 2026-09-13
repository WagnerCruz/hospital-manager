package com.raidstack.controllers.handlers;

import com.raidstack.services.exceptions.ResourceExceptionDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(ResourceExceptionDefault.class)
    public ResponseEntity<ProblemDetail> handlerResourceException(ResourceExceptionDefault exception) {
        ProblemDetail problem = ProblemDetail.forStatus(exception.getHttpStatus());
        problem.setTitle(exception.getTitulo());
        problem.setDetail(exception.getMessage());
        problem.setProperty("timestamp", LocalDateTime.now());
        if (Objects.nonNull(exception.getErrors()) && !exception.getErrors().isEmpty()) {
            problem.setProperty("errors", exception.getErrors());
        }
        return ResponseEntity.status(exception.getHttpStatus()).body(problem);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handlerMethodNotValidException(MethodArgumentNotValidException exception) {
        var status = HttpStatus.BAD_REQUEST;
        ProblemDetail problem = ProblemDetail.forStatus(status);
        problem.setTitle("Requisição incorreta");
        problem.setDetail(exception.getMessage());
        List<String> errors = new ArrayList<>();
        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        problem.setProperty("errors", errors);
        problem.setProperty("timestamp", LocalDateTime.now());
        return ResponseEntity.status(status).body(problem);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ProblemDetail> handlerRuntimeException(RuntimeException exception) {
        var status = HttpStatus.NOT_FOUND;
        ProblemDetail problem = ProblemDetail.forStatus(status);
        problem.setTitle("Erro interno do servidor");
        problem.setDetail(exception.getMessage());
        problem.setProperty("timestamp", LocalDateTime.now());
        return ResponseEntity.status(status).body(problem);
    }

}
