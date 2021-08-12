package com.max.tech.payment.web;

import com.stripe.exception.StripeException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@ControllerAdvice(annotations = RestController.class)
public class WebExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<WebError> catchMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new WebError(
                        HttpStatus.BAD_REQUEST,
                        LocalDateTime.now(),
                        formInvalidMessage(ex.getBindingResult())
                )
        );
    }

    @ExceptionHandler({DataAccessException.class, StripeException.class})
    private ResponseEntity<WebError> catchInternalError() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new WebError(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        LocalDateTime.now(),
                        "Internal error")
        );
    }

    private String formInvalidMessage(BindingResult bindingResult) {
        var reason = new StringBuilder();
        bindingResult.getAllErrors().forEach(e -> reason.append(errorToString(e)));
        return reason.toString();
    }

    private String errorToString(ObjectError error) {
        if (error instanceof FieldError fieldError) {
            return "field error: "
                    + fieldError.getField() +
                    " - " + fieldError.getDefaultMessage()
                    + ", actual value: ["
                    + fieldError.getRejectedValue() + "]; ";
        }
        return "object error: "
                + error.getObjectName()
                + " - " + error.getDefaultMessage() + "; ";
    }
}
