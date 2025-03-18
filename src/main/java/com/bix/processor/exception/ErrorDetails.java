package com.bix.processor.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ErrorDetails {
    private String message;
    private List<String> errors;
}