package com.max.tech.catalog.catalog.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@RestControllerAdvice
public class WebExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    private ResponseEntity<WebError> catchMethodArgumentNotValidException(ConstraintViolationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new WebError(
                        HttpStatus.BAD_REQUEST,
                        LocalDateTime.now(),
                        formInvalidMessage(ex.getConstraintViolations())
                )
        );
    }

    private String formInvalidMessage(Set<ConstraintViolation<?>> constraintViolations) {
        return constraintViolations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
    }

}
