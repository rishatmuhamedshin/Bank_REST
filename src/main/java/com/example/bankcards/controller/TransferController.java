package com.example.bankcards.controller;

import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.dto.TransferResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Управление переводом")
public interface TransferController {

    @Operation(
            summary = "Перевод между своими картами",
            description = "Доступно для USER. Переводит средства между картами текущего пользователя.",
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Перевод выполнен успешно",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TransferResponse.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные", content = @Content),
            @ApiResponse(responseCode = "401", description = "Неавторизован", content = @Content),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content),
            @ApiResponse(responseCode = "404", description = "Карта не найдена или недостаточно средств", content = @Content)
    })
    ResponseEntity<TransferResponse> transferBetweenMyCards(
            @RequestBody @Valid TransferRequest request,
            Authentication authentication);
}
