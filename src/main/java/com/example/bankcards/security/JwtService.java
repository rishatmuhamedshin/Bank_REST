package com.example.bankcards.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j
public class JwtService {

    @Value("${security.jwt.secret}")
    private String jwtSigningKey;

    @Value("${security.jwt.expiration}")
    private long jwtLifeTime;

    public boolean validateToken(String token, UserDetails userDetails){
        final String userName = extractUsername(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String generateToken(UserDetails userDetails){
        Map<String,Object> claims = new HashMap<>();

        if(userDetails instanceof AppUserDetails customUserDetails){
            claims.put("username", customUserDetails.user().getUsername());
            claims.put("email", customUserDetails.user().getEmail());
            claims.put("role", customUserDetails.user().getRole());
        }

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtLifeTime))
                .signWith(getSingingKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token){
        return extractsClaim(token, Claims::getExpiration);
    }

    public String extractUsername(String token){
        return extractsClaim(token, Claims::getSubject);
    }

    private Claims extractsClaims(String token){
        return Jwts.parser()
                .setSigningKey(getSingingKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private <T> T extractsClaim(String token, Function<Claims, T> claimsResolvers){
        final Claims claims = extractsClaims(token);
        return claimsResolvers.apply(claims);
    }

    private Key getSingingKey(){
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
