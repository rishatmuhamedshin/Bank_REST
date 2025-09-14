package com.example.bankcards.service.impl;

import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.dto.TransferResponse;
import com.example.bankcards.entity.BankCard;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enumeration.CardStatus;
import com.example.bankcards.exception.exceptions.BalanceException;
import com.example.bankcards.exception.exceptions.TransferException;
import com.example.bankcards.exception.exceptions.UserCardException;
import com.example.bankcards.repository.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class TransferServiceImplTest {

    @Autowired
    private TransferServiceImpl transferService;

    @MockBean
    private CardRepository cardRepository;

    private User user;
    private BankCard senderCard;
    private BankCard recipientCard;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("Rishat")
                .build();

        senderCard = BankCard.builder()
                .id(1L)
                .cardNumber("4111111111111111")
                .balance(BigDecimal.valueOf(1000))
                .status(CardStatus.ACTIVE)
                .user(user)
                .expiryDate(LocalDate.now().plusYears(1))
                .build();

        recipientCard = BankCard.builder()
                .id(2L)
                .cardNumber("4222222222222222")
                .balance(BigDecimal.valueOf(500))
                .status(CardStatus.ACTIVE)
                .user(user)
                .expiryDate(LocalDate.now().plusYears(1))
                .build();
    }

    @Test
    void testTransferBalance() {
        TransferRequest request = new TransferRequest();
        request.setSenderCardId(1L);
        request.setRecipientCardId(2L);
        request.setTransferAmount(BigDecimal.valueOf(200));

        when(cardRepository.findByIdWithLock(1L)).thenReturn(Optional.of(senderCard));
        when(cardRepository.findByIdWithLock(2L)).thenReturn(Optional.of(recipientCard));
        when(cardRepository.save(any(BankCard.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransferResponse response = transferService.transferBalance(request, "Rishat");

        assertEquals(BigDecimal.valueOf(800), senderCard.getBalance());
        assertEquals(BigDecimal.valueOf(700), recipientCard.getBalance());
        assertEquals("Rishat", response.getCardHolderName());
        verify(cardRepository, times(1)).save(senderCard);
        verify(cardRepository, times(1)).save(recipientCard);
    }

    @Test
    void testTransferThrows() {
        TransferRequest request = new TransferRequest();
        request.setSenderCardId(1L);
        request.setRecipientCardId(1L);
        request.setTransferAmount(BigDecimal.valueOf(100));

        when(cardRepository.findByIdWithLock(1L)).thenReturn(Optional.of(senderCard));

        assertThrows(TransferException.class,
                () -> transferService.transferBalance(request, "Rishat"));
    }

    @Test
    void testTransferInsufficientBalance() {
        TransferRequest request = new TransferRequest();
        request.setSenderCardId(1L);
        request.setRecipientCardId(2L);
        request.setTransferAmount(BigDecimal.valueOf(2000));

        when(cardRepository.findByIdWithLock(1L)).thenReturn(Optional.of(senderCard));
        when(cardRepository.findByIdWithLock(2L)).thenReturn(Optional.of(recipientCard));

        assertThrows(BalanceException.class,
                () -> transferService.transferBalance(request, "Rishat"));
    }

    @Test
    void testTransferInactiveCard() {
        senderCard.setStatus(CardStatus.BLOCKED);

        TransferRequest request = new TransferRequest();
        request.setSenderCardId(1L);
        request.setRecipientCardId(2L);
        request.setTransferAmount(BigDecimal.valueOf(100));

        when(cardRepository.findByIdWithLock(1L)).thenReturn(Optional.of(senderCard));
        when(cardRepository.findByIdWithLock(2L)).thenReturn(Optional.of(recipientCard));

        assertThrows(TransferException.class,
                () -> transferService.transferBalance(request, "Rishat"));
    }

    @Test
    void testTransferCardUserThrows() {
        User otherUser = User.builder().id(2L).username("NewUser").build();
        recipientCard.setUser(otherUser);

        TransferRequest request = new TransferRequest();
        request.setSenderCardId(1L);
        request.setRecipientCardId(2L);
        request.setTransferAmount(BigDecimal.valueOf(100));

        when(cardRepository.findByIdWithLock(1L)).thenReturn(Optional.of(senderCard));
        when(cardRepository.findByIdWithLock(2L)).thenReturn(Optional.of(recipientCard));

        assertThrows(UserCardException.class,
                () -> transferService.transferBalance(request, "Rishat"));
    }
}
