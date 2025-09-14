package com.example.bankcards.service;

import com.example.bankcards.dto.UpdatedUserRequest;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.dto.security.UserDTO;
import com.example.bankcards.entity.User;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    User create(UserDTO user);


    Page<UserDto> getAllUsers(Pageable pageable);

    void deleteUserById(Long userId);

    UserDto updateUser(Long userId, @Valid UpdatedUserRequest updatedUserRequest);

    UserDto setAdminRole(Long userId);
}
