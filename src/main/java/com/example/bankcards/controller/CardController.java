package com.example.bankcards.controller;

import com.example.bankcards.dto.*;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/card")
@RequiredArgsConstructor
@Tag(name = "Управление картами")
public class CardController {

    private final CardService cardService;

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
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<CardDto>> getAllCards(
            @Parameter(description = "Номер страницы (начиная с 0)", example = "1")
            @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(description = "Размер страницы", example = "10")
            @RequestParam(required = false, defaultValue = "10") int size) {
        return ResponseEntity.ok(cardService.getAllCards(PageRequest.of(page,size)));
    }

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

    @PostMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PageResponse<CardDto>> getMyCards(@RequestBody @Valid CardSearchRequest cardSearchRequest,
                                                    Authentication authentication) {
        String username = authentication.getName();

        PageResponse<CardDto> response = cardService.getUserCards(username, cardSearchRequest);

        return ResponseEntity.ok(response);
    }

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
    @GetMapping("/{cardId}/balance")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BalanceInfoDto > getBalance(@PathVariable Long cardId,
                                                      Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(cardService.getCardBalance(cardId,username));
    }

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
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardDto> createCard(@RequestBody @Valid CreateCardRequest request) {
        return ResponseEntity.ok(cardService.createNewCard(request));
    }

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
    @PatchMapping("/{cardId}/block")
    public ResponseEntity<Void> blockCard(@Parameter(description = "ID карты", example = "123") @PathVariable Long cardId,
                                          @Valid @RequestBody BlockRequestDto requestDto,
                                          Authentication authentication) {
        String username = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        if(isAdmin){
            cardService.processBlockRequest(requestDto, cardId);
        }else {
            cardService.createBlockRequest(requestDto,username,cardId);
        }
        return ResponseEntity.ok().build();
    }

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
    @PatchMapping("/{cardId}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardDto> activateCard(@PathVariable Long cardId) {
        return ResponseEntity.ok(cardService.activateCard(cardId));
    }

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
    @DeleteMapping("/{cardId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCard(@PathVariable Long cardId) {
        cardService.deleteCard(cardId);
        return ResponseEntity.ok().build();
    }

}
