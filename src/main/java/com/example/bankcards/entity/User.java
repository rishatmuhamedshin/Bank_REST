package com.example.bankcards.entity;

import com.example.bankcards.entity.enumeration.Role;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

import java.io.Serializable;

/**
 * Сущность пользователя приложения.
 * Содержит учётные данные, роль и список принадлежащих банковских карт.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @OneToMany(mappedBy = "user",  cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BankCard> cards;
}
