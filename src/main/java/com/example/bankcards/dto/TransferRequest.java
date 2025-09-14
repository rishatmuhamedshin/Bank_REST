package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TransferRequest {
    @Schema(description = "ID карты-отправителя", example = "123")
    @NotNull(message = "ID карты-отправителя обязателен")
    private Long recipientCardId;

    @Schema(description = "ID карты-получателя", example = "456")
    @NotNull(message = "ID карты-получателя обязателен")
    private Long senderCardId;

    @Schema(description = "Сумма перевода", example = "100.50")
    @NotNull(message = "Сумма обязателена")
    @DecimalMin(value = "0.01", message = "Сумма должна быть больше 0")
    private BigDecimal transferAmount;
}
