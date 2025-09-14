package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "Параметры поиска карт")
public class CardSearchRequest {

    @Schema(description = "Строка поиска (например, по номеру карты)", example = "4111111111111111")
    private String searchQuery;

    @Schema(description = "Номер страницы (начиная с 0)", example = "0", defaultValue = "0")
    @Min(value = 0, message = "Номер страницы не может быть меньше 0")
    private int page = 0;

    @Schema(description = "Размер страницы", example = "10", defaultValue = "10")
    @Min(value = 1, message = "Размер страницы должен быть хотя бы 1")
    private int size = 10;

    @Schema(description = "Поле сортировки", example = "id", defaultValue = "id")
    @NotBlank(message = "Поле сортировки обязательно")
    private String sortBy = "id";

    @Schema(description = "Направление сортировки", example = "asc", defaultValue = "asc")
    @Pattern(regexp = "asc|desc", flags = Pattern.Flag.CASE_INSENSITIVE, message = "sortDirection должен быть 'asc' или 'desc'")
    private String sortDirection = "asc";
}

