package com.example.bankcards.service.impl;

import com.example.bankcards.dto.security.JwtAuthenticationResponse;
import com.example.bankcards.dto.security.SignInRequest;
import com.example.bankcards.dto.security.SignUpRequest;
import com.example.bankcards.dto.security.UserDTO;
import com.example.bankcards.entity.enumeration.Role;
import com.example.bankcards.security.AppUserDetails;
import com.example.bankcards.security.JwtService;
import com.example.bankcards.service.AuthenticationService;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    @Override
    public JwtAuthenticationResponse signUp(SignUpRequest request) {
        UserDTO user = UserDTO.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .build();

        var savedUser = userService.create(user);
        var userDetails = new AppUserDetails(savedUser);
        String token = jwtService.generateToken(userDetails);

        log.info("Пользователь ({}) успешно зарегистрирован", savedUser.getUsername());

        return new JwtAuthenticationResponse(token);
    }

    @Transactional
    @Override
    public JwtAuthenticationResponse signIn(SignInRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtService.generateToken((UserDetails) authentication.getPrincipal());

        log.info("Пользователь ({}) успешно авторизован", request.getUsername());

        return new JwtAuthenticationResponse(token);
    }
}

