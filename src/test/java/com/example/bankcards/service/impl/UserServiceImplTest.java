package com.example.bankcards.service.impl;

import com.example.bankcards.dto.UpdatedUserRequest;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.dto.security.UserDTO;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enumeration.Role;
import com.example.bankcards.exception.exceptions.EmailException;
import com.example.bankcards.exception.exceptions.UserAccessException;
import com.example.bankcards.exception.exceptions.UserNotFoundException;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceImplTest {

    @Autowired
    private UserServiceImpl userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private UserDTO userDTO;
    private User user;

    @BeforeEach
    void setUp() {
        userDTO = UserDTO.builder()
                .username("Rishat")
                .email("Rishat@example.com")
                .password("mySuperPuperParol12314%$")
                .role(Role.ROLE_USER)
                .build();

        user = User.builder()
                .id(1L)
                .username("Rishat")
                .email("Rishat@example.com")
                .password("encodedPassword")
                .role(Role.ROLE_USER)
                .build();
    }

    @Test
    void testCreateUser() {
        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(userDTO.getUsername())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User createdUser = userService.create(userDTO);

        assertEquals(userDTO.getUsername(), createdUser.getUsername());
        assertEquals(userDTO.getEmail(), createdUser.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUserEmailExists() {
        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(true);

        assertThrows(EmailException.class, () -> userService.create(userDTO));
    }

    @Test
    void testCreateUserUsernameExists() {
        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(userDTO.getUsername())).thenReturn(true);

        assertThrows(UserAccessException.class, () -> userService.create(userDTO));
    }

    @Test
    void testUpdateUser() {
        UpdatedUserRequest updateRequest = new UpdatedUserRequest();
        updateRequest.setUsername("RishatNew");
        updateRequest.setEmail("RishatNew@example.com");
        updateRequest.setPassword("newPassword");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(updateRequest.getPassword())).thenReturn("newPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto updated = userService.updateUser(1L, updateRequest);

        assertEquals(updateRequest.getUsername(), updated.getUsername());
        assertEquals(updateRequest.getEmail(), updated.getEmail());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdateUserNotFoundThrows() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UpdatedUserRequest updateRequest = new UpdatedUserRequest();
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(1L, updateRequest));
    }

    @Test
    void testSetAdminRole() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.setAdminRole(1L);

        assertEquals(Role.ROLE_ADMIN, result.getRole());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testSetAdminRoleUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.setAdminRole(1L));
    }

    @Test
    void testDeleteUser() {
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUserById(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }
}
