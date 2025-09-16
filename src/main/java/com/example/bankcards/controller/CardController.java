package com.example.bankcards.controller;

import com.example.bankcards.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Управление картами")
public interface CardController {

    @Operation(
            summary = "Получить список карт (только для ADMIN)",
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список карт получен",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content),
            @ApiResponse(responseCode = "401", description = "Неавторизован", content = @Content)
    })
    ResponseEntity<PageResponse<CardDto>> getAllCards(
            @Parameter(description = "Номер страницы (начиная с 0)", example = "0")
            @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(description = "Размер страницы", example = "10")
            @RequestParam(required = false, defaultValue = "10") int size);


    @Operation(
            summary = "Получить карты текущего пользователя (только для USER)",
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список карт пользователя",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageResponse.class))),
            @ApiResponse(responseCode = "401", description = "Неавторизован", content = @Content),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content)
    })
    ResponseEntity<PageResponse<CardDto>> getMyCards(@RequestBody @Valid CardSearchRequest cardSearchRequest,
                                                     Authentication authentication);

    @Operation(
            summary = "Получить баланс карты (только для USER)",
            description = "Доступно для USER. Возвращает баланс и данные карты.",
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Информация о балансе карты",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BalanceInfoDto.class))),
            @ApiResponse(responseCode = "401", description = "Неавторизован", content = @Content),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content),
            @ApiResponse(responseCode = "404", description = "Карта не найдена", content = @Content)
    })
    ResponseEntity<BalanceInfoDto> getBalance(@PathVariable Long cardId,
                                              Authentication authentication);

    @Operation(
            summary = "Создать новую карту (только для ADMIN)",
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Карта успешно создана",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CardDto.class))),
            @ApiResponse(responseCode = "400", description = "Пользователя с таким именем не существует", content = @Content),
            @ApiResponse(responseCode = "400", description = "Карта с таким номером существует", content = @Content),
            @ApiResponse(responseCode = "401", description = "Неавторизован", content = @Content),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content)
    })
    ResponseEntity<CardDto> createCard(@RequestBody @Valid CreateCardRequest request);

    @Operation(
            summary = "Заблокировать карту ",
            description = "Доступно для USER (создание запроса) и ADMIN (мгновенная блокировка)",
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запрос на блокировку создан или карта заблокирована"),
            @ApiResponse(responseCode = "400", description = "Пользователя с таким именем не существует"),
            @ApiResponse(responseCode = "401", description = "Неавторизован", content = @Content),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content),
            @ApiResponse(responseCode = "404", description = "Карта не найдена", content = @Content)
    })
    ResponseEntity<Void> blockCard(@Parameter(description = "ID карты", example = "123") @PathVariable Long cardId,
                                   @Valid @RequestBody BlockRequestDto requestDto,
                                   Authentication authentication);

    @Operation(
            summary = "Активировать карту (только для ADMIN)",
            description = "Доступно только для ADMIN. Переводит карту в статус ACTIVE.",
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Карта успешно активирована",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CardDto.class))),
            @ApiResponse(responseCode = "401", description = "Неавторизован", content = @Content),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content),
            @ApiResponse(responseCode = "404", description = "Карта не найдена", content = @Content)
    })
    ResponseEntity<CardDto> activateCard(@PathVariable Long cardId);

    @Operation(
            summary = "Удалить карту (только для ADMIN)",
            description = "Доступно только для ADMIN",
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Карта успешно удалена", content = @Content),
            @ApiResponse(responseCode = "401", description = "Неавторизован", content = @Content),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content),
            @ApiResponse(responseCode = "404", description = "Карта не найдена", content = @Content)
    })
    ResponseEntity<Void> deleteCard(@PathVariable Long cardId);
}
