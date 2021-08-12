package com.max.tech.catalog.catalog.web;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record WebError(HttpStatus status,
                       @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
                       LocalDateTime timestamp,
                       String message) {
}
