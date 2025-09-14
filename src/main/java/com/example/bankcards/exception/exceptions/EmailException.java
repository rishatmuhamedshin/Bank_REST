package com.example.bankcards.exception.exceptions;

import com.example.bankcards.exception.ApiException;
import org.springframework.http.HttpStatus;

public class EmailException extends ApiException {

    public EmailException(String message) {
        super("BAD_REQUEST",
                message,
                HttpStatus.BAD_REQUEST
        );
    }
}
