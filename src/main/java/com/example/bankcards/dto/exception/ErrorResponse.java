package com.example.bankcards.dto.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class ErrorResponse {
    private String code;
    private String message;
    private LocalDateTime timestamp;
}
