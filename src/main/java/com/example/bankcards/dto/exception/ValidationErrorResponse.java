package com.example.bankcards.dto.exception;

import java.time.LocalDateTime;
import java.util.List;

public class ValidationErrorResponse extends ErrorResponse{
    private List<FieldErrorDetail> errors;

    public List<FieldErrorDetail> getErrors() {
        return errors;
    }

    public void setErrors(List<FieldErrorDetail> errors) {
        this.errors = errors;
    }

    public ValidationErrorResponse(String code, String message, LocalDateTime timestamp, List<FieldErrorDetail> errors) {
        super(code, message, timestamp);
        this.errors = errors;
    }
}
