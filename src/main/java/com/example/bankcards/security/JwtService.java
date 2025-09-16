package com.example.bankcards.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Сервис для выпуска и валидации JWT-токенов.
 * Использует симметричную подпись HS256 с настраиваемым секретом и временем жизни.
 */
@Service
@Slf4j
public class JwtService {

    @Value("${security.jwt.secret}")
    private String jwtSigningKey;

    @Value("${security.jwt.expiration}")
    private long jwtLifeTime; // в миллисекундах

    /**
     * Генерация JWT токена для пользователя
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof AppUserDetails customUserDetails) {
            claims.put("username", customUserDetails.user().getUsername());
            claims.put("email", customUserDetails.user().getEmail());
            claims.put("role", customUserDetails.user().getRole());
        }

        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtLifeTime))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Проверка валидности токена
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * Извлечение username из токена
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Проверка истечения токена
     */
    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    /**
     * Извлечение конкретного значения из Claims
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Разбор токена и проверка подписи
     */
    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Формирование ключа из Base64
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
