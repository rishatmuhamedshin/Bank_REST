package com.example.bankcards.service;

import com.example.bankcards.entity.BankCard;
import com.example.bankcards.entity.enumeration.CardStatus;
import com.example.bankcards.repository.CardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardExpiryScheduler {

    private final CardRepository cardRepository;

    @Transactional
    @Scheduled(cron = "0 0 0 * * ?")
    public void checkExpiredCards() {
        List<BankCard> activeCards = cardRepository.findByStatus(CardStatus.ACTIVE);

        for (BankCard card : activeCards) {
            if (card.getExpiryDate().isBefore(LocalDate.now()) || card.getExpiryDate().isEqual(LocalDate.now())) {
                card.setStatus(CardStatus.EXPIRED);
            }
        }

        cardRepository.saveAll(activeCards);
        log.info("Обновили статус у истекших карт " + LocalDateTime.now());
    }
}