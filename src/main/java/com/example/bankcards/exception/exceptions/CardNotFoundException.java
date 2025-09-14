package com.example.bankcards.exception.exceptions;

import com.example.bankcards.exception.ApiException;
import org.springframework.http.HttpStatus;

public class CardNotFoundException extends ApiException {

    public CardNotFoundException(String message) {
        super("NOT_FOUND",
                message,
                HttpStatus.NOT_FOUND
        );
    }
}