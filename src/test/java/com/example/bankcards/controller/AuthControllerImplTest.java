package com.example.bankcards.controller;

import com.example.bankcards.controller.impl.AuthControllerImpl;
import com.example.bankcards.dto.security.JwtAuthenticationResponse;
import com.example.bankcards.dto.security.SignInRequest;
import com.example.bankcards.dto.security.SignUpRequest;
import com.example.bankcards.security.JwtAuthFilter;
import com.example.bankcards.security.JwtService;
import com.example.bankcards.service.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthControllerImpl.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private AuthenticationService authenticationService;

    @Test
    void singIn() throws Exception {
        JwtAuthenticationResponse response = new JwtAuthenticationResponse();
        response.setToken("token123");

        when(authenticationService.signIn(any(SignInRequest.class)))
                .thenReturn(response);

        SignInRequest request = new SignInRequest();
        request.setUsername("Rishat");
        request.setPassword("password");

        mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
    }

    @Test
    void signUp() throws Exception {
        JwtAuthenticationResponse response = new JwtAuthenticationResponse();
        response.setToken("token123");

        when(authenticationService.signUp(any(SignUpRequest.class)))
                .thenReturn(response);

        SignUpRequest request = new SignUpRequest();
        request.setUsername("Rishat");
        request.setPassword("password123$");
        request.setEmail("newuser@course.ru");

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value("token123"));
    }
}
