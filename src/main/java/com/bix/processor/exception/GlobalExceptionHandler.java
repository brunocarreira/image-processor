package com.bix.processor.exception;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({BadRequestException.class, RuntimeException.class})
    @ResponseBody
    ErrorDetails onBadRequestException(BadRequestException e, HttpServletResponse response) {
        response.setStatus(e.getStatus());
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setMessage(e.getMessage());
        errorDetails.setErrors(e.getErrors());
        return errorDetails;
    }
}