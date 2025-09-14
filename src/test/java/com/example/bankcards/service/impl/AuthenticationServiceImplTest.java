package com.example.bankcards.service.impl;

import com.example.bankcards.dto.security.JwtAuthenticationResponse;
import com.example.bankcards.dto.security.SignInRequest;
import com.example.bankcards.dto.security.SignUpRequest;
import com.example.bankcards.dto.security.UserDTO;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enumeration.Role;
import com.example.bankcards.security.AppUserDetails;
import com.example.bankcards.security.JwtService;
import com.example.bankcards.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class AuthenticationServiceImplTest {

    @Autowired
    private AuthenticationServiceImpl authenticationService;

    @MockBean
    private UserService userService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Test
    void testSignUp() {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("Rishat");
        request.setEmail("Rishat@example.com");
        request.setPassword("StrongPass123!");

        var savedUser = User.builder()
                .id(1L)
                .username("Rishat")
                .password("encodedPassword")
                .role(Role.ROLE_USER)
                .build();

        when(passwordEncoder.encode("StrongPass123!")).thenReturn("encodedPassword");
        when(userService.create(any(UserDTO.class))).thenReturn(savedUser);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("token123");

        JwtAuthenticationResponse response = authenticationService.signUp(request);

        assertNotNull(response);
        assertEquals("token123", response.getToken());
        verify(userService, times(1)).create(any(UserDTO.class));
        verify(jwtService, times(1)).generateToken(any(UserDetails.class));
    }

    @Test
    void testSignIn() {
        SignInRequest request = new SignInRequest();
        request.setUsername("Rishat");
        request.setPassword("StrongPass123!");

        UserDetails userDetails = new AppUserDetails(User.builder()
                .id(1L)
                .username("Rishat")
                .password("password")
                .role(Role.ROLE_USER)
                .build());

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("token123");

        JwtAuthenticationResponse response = authenticationService.signIn(request);

        assertNotNull(response);
        assertEquals("token123", response.getToken());
        verify(authenticationManager, times(1)).authenticate(any());
        verify(jwtService, times(1)).generateToken(any(UserDetails.class));
    }
}
