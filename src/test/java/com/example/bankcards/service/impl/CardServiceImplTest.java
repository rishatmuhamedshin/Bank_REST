package com.example.bankcards.service.impl;

import com.example.bankcards.dto.BalanceInfoDto;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.entity.BankCard;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enumeration.CardStatus;
import com.example.bankcards.entity.enumeration.Role;
import com.example.bankcards.exception.exceptions.CardStatusException;
import com.example.bankcards.exception.exceptions.UserNotFoundException;
import com.example.bankcards.repository.CardBlockRequestRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class CardServiceImplTest {

    @Autowired
    private CardServiceImpl cardService;

    @MockBean
    private CardRepository cardRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private CardBlockRequestRepository blockRequestRepository;

    private User user;
    private BankCard activeCard;
    private BankCard blocked;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("Rishat123")
                .email("Rishat@example.com")
                .role(Role.ROLE_USER)
                .build();

        activeCard = BankCard.builder()
                .id(1L)
                .cardNumber("4111111111111111")
                .balance(BigDecimal.valueOf(1000))
                .user(user)
                .status(CardStatus.ACTIVE)
                .expiryDate(LocalDate.now().plusYears(1))
                .build();

        blocked = BankCard.builder()
                .id(2L)
                .cardNumber("4111111111111111")
                .balance(BigDecimal.valueOf(1000))
                .user(user)
                .status(CardStatus.BLOCKED)
                .expiryDate(LocalDate.now().plusYears(1))
                .build();
    }

    @Test
    void testCreateNewCard() {
        CreateCardRequest request = new CreateCardRequest();
        request.setUsername("Rishat");
        request.setCardNumber("4111111111111111");
        request.setBalance(BigDecimal.valueOf(1000));
        request.setExpiryDate(LocalDate.now().plusYears(1));
        request.setCardStatus(CardStatus.ACTIVE);

        when(userRepository.findByUsername("Rishat")).thenReturn(Optional.of(user));
        when(cardRepository.existsByCardNumber("4111111111111111")).thenReturn(false);
        when(cardRepository.save(any(BankCard.class))).thenReturn(activeCard);

        CardDto result = cardService.createNewCard(request);

        assertNotNull(result);
        assertEquals("**** **** **** 1111", result.getCardNumber());
        verify(cardRepository, times(1)).save(any(BankCard.class));
    }

    @Test
    void testCreateNewCardUserNotFound() {
        CreateCardRequest request = new CreateCardRequest();
        request.setUsername("unknown");

        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> cardService.createNewCard(request));
    }

    @Test
    void testActivateCard() {
        when(cardRepository.findById(2L)).thenReturn(Optional.of(blocked));
        when(cardRepository.save(any(BankCard.class))).thenReturn(blocked);

        CardDto result = cardService.activateCard(2L);

        assertNotNull(result);
        assertEquals(CardStatus.ACTIVE, blocked.getStatus());
    }

    @Test
    void testActivateCardAlreadyActive() {
        activeCard.setStatus(CardStatus.ACTIVE);
        when(cardRepository.findById(1L)).thenReturn(Optional.of(activeCard));

        assertThrows(CardStatusException.class,
                () -> cardService.activateCard(1L));
    }

    @Test
    void testGetCardBalance() {
        when(cardRepository.findBalanceByCardIdAndUsername(1L, "Rishat123"))
                .thenReturn(Optional.of(activeCard));

        BalanceInfoDto balance = cardService.getCardBalance(1L, "Rishat123");

        assertNotNull(balance);
        assertEquals(BigDecimal.valueOf(1000), balance.getBalance());
    }

    @Test
    void testGetCardBalanceInactiveCard() {
        activeCard.setStatus(CardStatus.BLOCKED);
        when(cardRepository.findBalanceByCardIdAndUsername(1L, "Rishat123"))
                .thenReturn(Optional.of(activeCard));

        assertThrows(CardStatusException.class,
                () -> cardService.getCardBalance(1L, "Rishat123"));
    }
}
