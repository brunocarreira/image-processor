package com.bix.processor.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

@Getter
public class BadRequestException extends RuntimeException {
    String message = "Bad Request";
    @Setter
    private List<String> errors;
    private int status = 400;

    public BadRequestException(String error) {
        errors = Collections.singletonList(error);
    }

    public BadRequestException(String error, String message) {
        errors = Collections.singletonList(error);
        this.message = message;
    }

    public BadRequestException(String error, String message, int status) {
        errors = Collections.singletonList(error);
        this.message = message;
        this.status = status;
    }

    public BadRequestException(String error, int status) {
        errors = Collections.singletonList(error);
        this.status = status;
    }

    public BadRequestException(List<String> errors) {
        this.setErrors(errors);
    }

    public BadRequestException(List<String> errors, int status) {
        this.setErrors(errors);
        this.status = status;
    }

}