package com.example.bankcards.controller.impl;

import com.example.bankcards.controller.UserController;
import com.example.bankcards.dto.UpdatedUserRequest;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для управления пользователями (доступен только ADMIN).
 */
@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {

    private final UserService userService;

    /**
     * Получить список всех пользователей с пагинацией и сортировкой.
     */
    @PostMapping
    public ResponseEntity<Page<UserDto>> getAllUsers(
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "10", required = false) int size,
            @RequestParam(defaultValue = "asc", required = false) String sortDir
    ) {
        Sort sort = Sort.by(
                sortDir.equalsIgnoreCase("desc")
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC,
                "username"
        );
        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    /**
     * Удалить пользователя.
     */
    @PatchMapping("/{userId}/delete")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUserById(userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Обновить данные пользователя.
     */
    @PutMapping("/{userId}/update")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long userId,
                                           @Valid @RequestBody UpdatedUserRequest updatedUserRequest) {
        return ResponseEntity.ok(userService.updateUser(userId,updatedUserRequest));
    }

    /**
     * Назначить пользователю роль ADMIN.
     */
    @PutMapping("/{userId}/newAdmin")
    public ResponseEntity<UserDto> setAdminRoleToUser( @PathVariable Long userId) {
        return ResponseEntity.ok(userService.setAdminRole(userId));
    }
}