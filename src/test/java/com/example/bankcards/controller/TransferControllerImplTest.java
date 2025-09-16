package com.example.bankcards.controller;

import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.dto.TransferResponse;
import com.example.bankcards.service.TransferService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class TransferControllerImplTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransferService transferService;

    private TransferRequest transferRequest;
    private TransferResponse transferResponse;

    @BeforeEach
    void setUp() {
        transferRequest = new TransferRequest();
        transferRequest.setSenderCardId(1L);
        transferRequest.setRecipientCardId(2L);
        transferRequest.setTransferAmount(BigDecimal.valueOf(100));

        transferResponse = new TransferResponse();
        transferResponse.setCardNumber("**** **** **** 1234");
        transferResponse.setBalance(BigDecimal.valueOf(500));
        transferResponse.setCardHolderName("IVANOV IVAN");
        transferResponse.setExpiryDate(LocalDate.now().plusDays(2));
    }

    @Test
    @WithMockUser(username = "Rishat", roles = {"USER"})
    void transferBetweenMyCards() throws Exception {
        when(transferService.transferBalance(eq(transferRequest), eq("Rishat")))
                .thenReturn(transferResponse);

        mockMvc.perform(post("/api/transfers/to-my-card")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cardNumber").value("**** **** **** 1234"))
                .andExpect(jsonPath("$.balance").value(500))
                .andExpect(jsonPath("$.cardHolderName").value("IVANOV IVAN"));
    }
}
