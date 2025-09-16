package com.example.bankcards.entity;

import com.example.bankcards.entity.enumeration.CardStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Сущность банковской карты пользователя.
 * Хранит баланс, статус, дату истечения и ссылку на владельца карты.
 */
@Entity
@Table(name = "cards")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BankCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_number", nullable = false, unique = true)
    private String cardNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CardStatus status;

    @Column(name = "balance", nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public boolean isExpired() {
        return expiryDate.isBefore(LocalDate.now());
    }
}
