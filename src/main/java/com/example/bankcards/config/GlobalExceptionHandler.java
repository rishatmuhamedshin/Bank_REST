package com.example.bankcards.config;

import com.example.bankcards.dto.exception.ErrorResponse;
import com.example.bankcards.dto.exception.FieldErrorDetail;
import com.example.bankcards.dto.exception.ValidationErrorResponse;
import com.example.bankcards.exception.ApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Глобальный обработчик исключений для всего приложения.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обрабатывает исключения типа {@link ApiException}.
     * <p>
     * Используется для бизнес-исключений, когда нужно вернуть клиенту
     * код ошибки, сообщение и HTTP-статус.
     * </p>
     *
     * @param ex выброшенное исключение {@link ApiException}
     * @return {@link ResponseEntity} с телом {@link ErrorResponse}
     *         и HTTP-статусом из исключения
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex) {
        ErrorResponse error = new ErrorResponse(
                ex.getCode(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(ex.getStatus()).body(error);
    }

    /**
     * Обрабатывает ошибки валидации аргументов контроллера.
     * <p>
     * Срабатывает, когда DTO, аннотированные {@code @Valid}, содержат некорректные данные.
     * Формирует список полей с ошибками:
     * <ul>
     *   <li>название поля,</li>
     *   <li>отклонённое значение,</li>
     *   <li>сообщение об ошибке.</li>
     * </ul>
     * </p>
     *
     * @param ex исключение {@link MethodArgumentNotValidException},
     *           содержащее информацию об ошибках валидации
     * @return {@link ResponseEntity} с телом {@link ValidationErrorResponse}
     *         и статусом {@code 400 Bad Request}
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<FieldErrorDetail> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new FieldErrorDetail(
                        fe.getField(),
                        fe.getRejectedValue(),
                        fe.getDefaultMessage()
                ))
                .collect(Collectors.toList());

        ValidationErrorResponse error = new ValidationErrorResponse(
                "VALIDATION_FAILED",
                "Ошибка валидации входных данных",
                LocalDateTime.now(),
                errors
        );
        return ResponseEntity.badRequest().body(error);
    }
}

