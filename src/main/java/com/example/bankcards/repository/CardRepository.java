package com.example.bankcards.repository;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.BankCard;
import com.example.bankcards.entity.enumeration.CardStatus;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<BankCard, Long>, JpaSpecificationExecutor<BankCard> {

    boolean existsByCardNumber(String cardNumber);

    @Query("SELECT c FROM BankCard c WHERE c.id = :cardId AND c.user.username = :username")
    Optional<BankCard> findBalanceByCardIdAndUsername(@Param("cardId") Long cardId,
                                                      @Param("username") String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM BankCard c WHERE c.id = :id")
    Optional<BankCard> findByIdWithLock(@Param("id") Long id);

    List<BankCard> findByStatus(CardStatus cardStatus);
}
