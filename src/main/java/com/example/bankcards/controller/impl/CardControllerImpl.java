package com.example.bankcards.controller.impl;

import com.example.bankcards.controller.CardController;
import com.example.bankcards.dto.*;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для управления банковскими картами.
 */
@RestController
@RequestMapping("api/card")
@RequiredArgsConstructor
public class CardControllerImpl implements CardController {

    private final CardService cardService;

    /**
     * Получить список всех карт (доступно только ADMIN).
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<CardDto>> getAllCards(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        return ResponseEntity.ok(cardService.getAllCards(PageRequest.of(page,size)));
    }

    /**
     * Получить список карт текущего пользователя (доступно только USER).
     */
    @PostMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PageResponse<CardDto>> getMyCards(@RequestBody @Valid CardSearchRequest cardSearchRequest,
                                                    Authentication authentication) {
        String username = authentication.getName();

        PageResponse<CardDto> response = cardService.getUserCards(username, cardSearchRequest);

        return ResponseEntity.ok(response);
    }

    /**
     * Получить баланс карты по ID (только USER).
     */
    @GetMapping("/{cardId}/balance")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BalanceInfoDto > getBalance(@PathVariable Long cardId,
                                                      Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(cardService.getCardBalance(cardId,username));
    }

    /**
     * Создать новую карту (только ADMIN).
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardDto> createCard(@RequestBody @Valid CreateCardRequest request) {
        return ResponseEntity.ok(cardService.createNewCard(request));
    }

    /**
     * Заблокировать карту (USER создаёт запрос, ADMIN блокирует сразу).
     */
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

    /**
     * Активировать карту (только ADMIN).
     */
    @PatchMapping("/{cardId}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardDto> activateCard(@PathVariable Long cardId) {
        return ResponseEntity.ok(cardService.activateCard(cardId));
    }

    /**
     * Удалить карту (только ADMIN).
     */
    @DeleteMapping("/{cardId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCard(@PathVariable Long cardId) {
        cardService.deleteCard(cardId);
        return ResponseEntity.ok().build();
    }
}
