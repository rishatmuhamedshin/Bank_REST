package com.example.bankcards.dto;

import com.example.bankcards.entity.enumeration.CardStatus;
import com.example.bankcards.util.validation.annoatation.CardNumber;
import com.example.bankcards.util.validation.annoatation.ValidCardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "Запрос на создание новой карты")
public class CreateCardRequest {

    @Schema(description = "Имя пользователя, которому принадлежит карта",
            example = "Rishat432")
    @NotBlank(message = "Имя пользователя обязательно")
    @Size(min = 5, max = 50, message = "Имя в пределах от 5 до 50 символов")
    private String username;

    @Schema(description = "Статус карты", example = "ACTIVE")
    @ValidCardStatus
    @NotNull(message = "Статус не может быть пустым")
    private CardStatus cardStatus;

    @Schema(description = "Баланс карты", example = "1000.50")
    @NotNull(message = "Баланс обязателен")
    @DecimalMin(value = "0.0", message = "Баланс не может быть меньше 0")
    private BigDecimal balance;

    @Schema(description = "Срок действия карты", example = "2026-12-31")
    @NotNull(message = "Срок действия карты обязателен")
    @Future(message = "Дата должна быть больше текущей")
    private LocalDate expiryDate;

    @Schema(description = "Номер карты", example = "4111111111111111")
    @CardNumber
    @NotNull(message = "Номер карты не может быть пустым")
    private String cardNumber;
}

