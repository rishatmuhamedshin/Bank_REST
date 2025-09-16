package com.example.bankcards.controller.impl;

import com.example.bankcards.controller.TransferController;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.dto.TransferResponse;
import com.example.bankcards.service.TransferService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для управления переводами между картами.
 */
@RestController
@RequestMapping("/api/transfers")
@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
public class TransferControllerImpl implements TransferController {

    private final TransferService transferService;


    /**
     * Перевод средств между картами текущего пользователя.
     */
    @PostMapping(value =  "/to-my-card", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransferResponse> transferBetweenMyCards(
            @RequestBody @Valid TransferRequest request,
            Authentication authentication) {
        String username = authentication.getName();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(transferService.transferBalance(request,username));
    }

}
