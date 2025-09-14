package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BlockRequestDto {

    @Schema(description = "Причина блокировки",
            example = "Карта утеряна. Требуется блокировка.",
            maxLength = 500)
    @NotBlank(message = "Причина блокировки обязательна")
    @Size(max = 500, message = "Причина не должна превышать 500 символов")
    private String reason;
}
