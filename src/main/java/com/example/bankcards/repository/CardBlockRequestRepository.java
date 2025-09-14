package com.example.bankcards.repository;

import com.example.bankcards.entity.BankCard;
import com.example.bankcards.entity.enumeration.BlockRequestStatus;
import com.example.bankcards.entity.CardBlockRequest;
import com.example.bankcards.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardBlockRequestRepository extends JpaRepository<CardBlockRequest, Long> {
    boolean existsByCardAndUserAndStatus(BankCard card, User user, BlockRequestStatus status);

    Optional<CardBlockRequest> findByCardId(Long cardId);
}
