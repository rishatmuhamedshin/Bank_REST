package com.example.bankcards.dto.exception;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FieldErrorDetail {
    private String field;
    private Object rejectedValue;
    private String message;
}
