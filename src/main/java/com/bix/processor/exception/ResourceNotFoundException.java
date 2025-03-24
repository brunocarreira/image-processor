package com.bix.processor.exception;

import lombok.Setter;

import java.util.Collections;
import java.util.List;

public class ResourceNotFoundException extends RuntimeException{
    String message = "Not Found";
    @Setter
    private List<String> errors;
    private int status = 404;

    public ResourceNotFoundException(String error) {
        errors = Collections.singletonList(error);
    }
}
