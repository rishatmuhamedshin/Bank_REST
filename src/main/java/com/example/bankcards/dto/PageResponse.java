package com.example.bankcards.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Schema(description = "Страничный ответ")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResponse<T> {
    List<T> content;

    @Schema(description = "Метаданные")
    Metadata metadata;

    @Setter
    @Getter
    @Schema(description = "Информация о странице")
    public static class Metadata {
        @Schema(description = "Номер страницы", example = "1")
        private int page;

        @Schema(description = "Размер страницы", example = "10")
        private int size;

        @Schema(description = "Общее количество элементов", example = "100")
        private long totalElements;

        @Schema(description = "Общее количество страниц", example = "10")
        private long totalPages;
    }
}
