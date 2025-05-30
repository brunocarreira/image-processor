package com.bix.processor.exception;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {
    private final String message;
    private final int status;

    public ResourceNotFoundException(String message) {
        this.status = 404;
        this.message = message;
    }
}
