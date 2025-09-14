package com.example.bankcards.dto;

import com.example.bankcards.entity.enumeration.CardStatus;
import com.example.bankcards.util.validation.annoatation.ValidCardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@Schema(description = "Информация о балансе карты")
@AllArgsConstructor
@NoArgsConstructor
public class BalanceInfoDto {

    @NotNull(message = "Баланс не может быть null")
    @DecimalMin(value = "0.0", message = "Баланс не может быть отрицательным")
    @Schema(description = "Баланс карты", example = "1234.56")
    private BigDecimal balance;

    @NotNull(message = "Статус карты не может быть null")
    @Schema(description = "Статус карты", example = "ACTIVE")
    @ValidCardStatus
    private CardStatus status;

    @NotBlank(message = "Номер карты не может быть пустым")
    @Size(min = 12, max = 19, message = "Номер карты должен содержать от 12 до 19 символов")
    @Schema(description = "Маскированный номер карты", example = "************1111")
    private String maskedCardNumber;

    @NotBlank(message = "Имя держателя карты не может быть пустым")
    @Size(max = 100, message = "Имя держателя карты не должно превышать 100 символов")
    @Schema(description = "Имя держателя карты", example = "IVAN IVANOV")
    private String cardHolderName;

    @NotNull(message = "Срок действия карты не может быть null")
    @FutureOrPresent(message = "Срок действия карты должен быть сегодня или в будущем")
    @Schema(description = "Срок действия карты", example = "2026-12-31")
    private LocalDate expiryDate;
}
