package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ после перевода")
@Setter
@Getter
public class TransferResponse {

    @Schema(description = "Баланс карты после перевода", example = "900.50")
    @NotNull(message = "Баланс не может быть пустым")
    private BigDecimal balance;

    @Schema(description = "Маскированный номер карты", example = "************1111")
    @NotBlank(message = "Маскированный номер карты обязателен")
    private String cardNumber;

    @Schema(description = "Срок действия карты", example = "2026-12-31")
    @NotNull(message = "Срок действия карты обязателен")
    private LocalDate expiryDate;

    @Schema(description = "Имя держателя карты", example = "IVAN IVANOV")
    @NotBlank(message = "Имя держателя карты обязательно")
    private String cardHolderName;
}

