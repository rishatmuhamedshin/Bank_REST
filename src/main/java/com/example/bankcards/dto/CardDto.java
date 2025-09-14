package com.example.bankcards.dto;

import com.example.bankcards.entity.enumeration.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Builder
@Data
@Schema(description = "Данные банковской карты")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardDto {

    @Schema(description = "ID карты", example = "123")
    @NotNull(message = "ID карты обязателен")
    private Long id;

    @Schema(description = "Номер карты", example = "4111111111111111")
    @NotBlank(message = "Номер карты обязателен")
    private String cardNumber;

    @Schema(description = "Статус карты", example = "ACTIVE")
    @NotNull(message = "Статус карты обязателен")
    private CardStatus status;

    @Schema(description = "Срок действия", example = "2026-12-31")
    @NotNull(message = "Срок действия карты обязателен")
    private LocalDate expiryDate;

    @Schema(description = "Имя держателя карты", example = "IVAN IVANOV")
    @NotBlank(message = "Имя держателя карты обязательно")
    private String cardHolderName;
}

