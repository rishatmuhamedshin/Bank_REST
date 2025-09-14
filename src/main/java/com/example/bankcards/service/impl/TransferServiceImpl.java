package com.example.bankcards.service.impl;

import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.dto.TransferResponse;
import com.example.bankcards.entity.BankCard;
import com.example.bankcards.entity.enumeration.CardStatus;
import com.example.bankcards.exception.exceptions.BalanceException;
import com.example.bankcards.exception.exceptions.CardNotFoundException;
import com.example.bankcards.exception.exceptions.TransferException;
import com.example.bankcards.exception.exceptions.UserCardException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.service.TransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static com.example.bankcards.util.CardEncryptUtil.maskCardNumber;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferServiceImpl implements TransferService {

    private final CardRepository cardRepository;

    @Override
    @Transactional
    public TransferResponse transferBalance(TransferRequest request,
                                            String username) {
        Long senderCardId = request.getSenderCardId();
        Long recipientCardId = request.getRecipientCardId();

        BankCard senderCard = cardRepository.findByIdWithLock(senderCardId)
                .orElseThrow(() -> new CardNotFoundException("Карта отправителя не найдена"));

        BankCard recipientCard = cardRepository.findByIdWithLock(recipientCardId)
                .orElseThrow(() -> new CardNotFoundException("Карта получателя не найдена"));

        validateTransfer(senderCard, recipientCard, request.getTransferAmount(), username);

        performTransfer(senderCard, recipientCard, request.getTransferAmount());

        log.info("Перевод с карты:{} на крату: {}", senderCardId, recipientCardId);


        return buildResponse(senderCard,username);
    }

    private void validateTransfer(BankCard sender, BankCard recipient, BigDecimal amount, String username) {
        if (sender.getId().equals(recipient.getId())) {
            throw new TransferException("Нельзя переводить на ту же карту");
        }
        if(!sender.getUser().getUsername().equals(username)){
            throw new UserCardException("Карта " + maskCardNumber(sender.getCardNumber()) + " вам не принадлежит");
        }

        if(!recipient.getUser().getUsername().equals(username)){
            throw new UserCardException("Карта " + maskCardNumber(recipient.getCardNumber()) + " вам не принадлежит");
        }

        if (sender.getStatus() != CardStatus.ACTIVE) {
            throw new TransferException("Карта отправителя не активна");
        }

        if (recipient.getStatus() != CardStatus.ACTIVE) {
            throw new TransferException("Карта получателя не активна");
        }

        if (sender.getBalance().compareTo(amount) < 0) {
            throw new BalanceException("Недостаточно средств. Доступно: " + sender.getBalance());
        }

        if (sender.isExpired()) {
            throw new TransferException("Срок действия карты отправителя истек");
        }

        if (recipient.isExpired()) {
            throw new TransferException("Срок действия карты получателя истек");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransferException("Сумма перевода должна быть положительной");
        }
    }

    private void performTransfer(BankCard sender, BankCard recipient, BigDecimal amount) {
        sender.setBalance(sender.getBalance().subtract(amount));
        recipient.setBalance(recipient.getBalance().add(amount));

        cardRepository.save(sender);
        cardRepository.save(recipient);
    }

    private TransferResponse buildResponse(BankCard senderCard, String username) {
        return TransferResponse.builder()
                .balance(senderCard.getBalance())
                .cardHolderName(username)
                .expiryDate(senderCard.getExpiryDate())
                .cardNumber(maskCardNumber(senderCard.getCardNumber()))
                .build();
    }
}
