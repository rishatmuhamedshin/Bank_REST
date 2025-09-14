package com.example.bankcards.exception.exceptions;

import com.example.bankcards.exception.ApiException;
import org.springframework.http.HttpStatus;

public class RequestNotFoundException extends ApiException {

    public RequestNotFoundException(String message) {
        super("NOT_FOUND",
                message,
                HttpStatus.NOT_FOUND
        );
    }
}