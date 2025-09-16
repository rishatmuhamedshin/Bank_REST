package com.example.bankcards.controller;

import com.example.bankcards.dto.*;
import com.example.bankcards.service.CardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.example.bankcards.entity.enumeration.CardStatus.ACTIVE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CardControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CardService cardService;

    private CardDto cardDto;
    private PageResponse<CardDto> pageResponse;
    private BalanceInfoDto balanceInfoDto;
    private CreateCardRequest createCardRequest;
    private BlockRequestDto blockRequestDto;

    @BeforeEach
    void setUp() {
        cardDto = CardDto.builder()
                .id(1L)
                .cardNumber("**** **** **** 1234")
                .expiryDate(LocalDate.now().plusDays(2))
                .status(ACTIVE)
                .cardHolderName("IVAN")
                .build();

        pageResponse = new PageResponse<>();
        pageResponse.setContent(List.of(cardDto));

        PageResponse.Metadata metadata = new PageResponse.Metadata();
        metadata.setPage(0);
        metadata.setSize(10);
        metadata.setTotalElements(1L);
        metadata.setTotalPages(1L);

        pageResponse.setMetadata(metadata);

        balanceInfoDto = BalanceInfoDto.builder()
                .balance(BigDecimal.valueOf(500))
                .maskedCardNumber("**** **** **** 1234")
                .status(ACTIVE)
                .cardHolderName("IVAN IVANOV")
                .expiryDate(LocalDate.now().plusDays(2))
                .build();

        createCardRequest = new CreateCardRequest();
        createCardRequest.setUsername("Rishat");
        createCardRequest.setCardNumber("1111222233334444");
        createCardRequest.setBalance(BigDecimal.valueOf(1000));
        createCardRequest.setExpiryDate(LocalDate.now().plusYears(2));
        createCardRequest.setCardStatus(ACTIVE);

        blockRequestDto = new BlockRequestDto();
        blockRequestDto.setReason("Потерял карту");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getAllCards() throws Exception {
        when(cardService.getAllCards(any(PageRequest.class)))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/api/card")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].cardNumber").value("**** **** **** 1234"))
                .andExpect(jsonPath("$.metadata.page").value(0));
    }

    @Test
    @WithMockUser(username = "IVAN", roles = {"USER"})
    void getMyCards() throws Exception {
        when(cardService.getUserCards(eq("IVAN"), any(CardSearchRequest.class)))
                .thenReturn(pageResponse);

        CardSearchRequest searchRequest = new CardSearchRequest();

        mockMvc.perform(post("/api/card/my")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].status").value("ACTIVE"));
    }


    @Test
    @WithMockUser(username = "IVAN IVANOV", roles = {"USER"})
    void getBalance() throws Exception{
        when(cardService.getCardBalance(eq(1L), eq("IVAN IVANOV"))).thenReturn(balanceInfoDto);

        mockMvc.perform(get("/api/card/1/balance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maskedCardNumber").value("**** **** **** 1234"))
                .andExpect(jsonPath("$.balance").value(500));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createCard() throws Exception {
        when(cardService.createNewCard(any(CreateCardRequest.class))).thenReturn(cardDto);

        mockMvc.perform(post("/api/card")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCardRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void blockCardAsAdmin() throws Exception{
        doNothing().when(cardService).processBlockRequest(any(BlockRequestDto.class), eq(1L));

        mockMvc.perform(patch("/api/card/1/block")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(blockRequestDto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void blockCardAsUser() throws Exception {
        doNothing().when(cardService).createBlockRequest(any(BlockRequestDto.class), eq("Rishat"), eq(1L));

        mockMvc.perform(patch("/api/card/1/block")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(blockRequestDto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void activateCard() throws Exception {
        when(cardService.activateCard(1L)).thenReturn(cardDto);

        mockMvc.perform(patch("/api/card/1/activate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteCard() throws Exception {
        doNothing().when(cardService).deleteCard(1L);

        mockMvc.perform(delete("/api/card/1"))
                .andExpect(status().isOk());
    }
}