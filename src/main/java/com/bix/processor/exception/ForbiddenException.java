package com.bix.processor.exception;

import lombok.Getter;

@Getter
public class ForbiddenException extends RuntimeException {
    private final String message;
    private final int status;

    public ForbiddenException(String message) {
        this.status = 403;
        this.message = message;
    }
}
