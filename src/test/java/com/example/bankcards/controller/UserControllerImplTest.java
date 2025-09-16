package com.example.bankcards.controller;

import com.example.bankcards.dto.UpdatedUserRequest;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enumeration.Role;
import com.example.bankcards.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("Rishat")
                .email("Rishat@example.com")
                .role(Role.ROLE_ADMIN)
                .password("12345")
                .build();

        userDto = UserDto.builder()
                .username("Rishat")
                .email("Rishat@example.com")
                .build();
    }

    @Test
    @WithMockUser(username = "Rishat", roles = {"ADMIN"})
    void getAllUsers() throws Exception{
        Page<UserDto> page = new PageImpl<>(List.of(userDto));
        when(userService.getAllUsers(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(post("/api/users")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].username").value("Rishat"))
                .andExpect(jsonPath("$.content[0].email").value("Rishat@example.com"));
    }
    @Test
    @WithMockUser(username = "Rishat", roles = {"ADMIN"})
    void deleteUser() throws Exception {
        doNothing().when(userService).deleteUserById(1L);

        mockMvc.perform(patch("/api/users/1/delete"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "Rishat", roles = {"ADMIN"})
    void updateUser() throws Exception {
        UpdatedUserRequest request = new UpdatedUserRequest();
        request.setUsername("newAdmin");
        request.setEmail("newadmin@example.com");

        when(userService.updateUser(eq(1L), any(UpdatedUserRequest.class))).thenReturn(userDto);

        mockMvc.perform(put("/api/users/1/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("Rishat"))
                .andExpect(jsonPath("$.email").value("Rishat@example.com"));
    }


    @Test
    @WithMockUser(username = "Rishat", roles = {"ADMIN"})
    void setAdminRoleToUser() throws Exception {
        when(userService.setAdminRole(1L)).thenReturn(userDto);

        mockMvc.perform(put("/api/users/1/newAdmin"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("Rishat"));
    }
}