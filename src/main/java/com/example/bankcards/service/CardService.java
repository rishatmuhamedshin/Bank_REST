package com.example.bankcards.service;

import com.example.bankcards.dto.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;

public interface CardService {

    PageResponse<CardDto> getAllCards(PageRequest pageRequest);

    PageResponse<CardDto> getUserCards(String username, @Valid CardSearchRequest cardSearchRequest);

    CardDto createNewCard(@Valid CreateCardRequest request);

    void processBlockRequest(@Valid BlockRequestDto requestDto, Long cardId);

    void createBlockRequest(@Valid BlockRequestDto requestDto, String username, Long cardId);

    CardDto activateCard(Long cardId);

    void deleteCard(Long cardId);

    BalanceInfoDto  getCardBalance(Long cardId, String username);
}
