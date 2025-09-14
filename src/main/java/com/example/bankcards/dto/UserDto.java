package com.example.bankcards.dto;

import com.example.bankcards.entity.enumeration.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@Schema(description = "Информация о пользователе")
public class UserDto {

    @Schema(description = "Имя пользователя", example = "ivan_ivanov")
    @NotBlank(message = "Имя пользователя обязательно")
    private String username;

    @Schema(description = "Email пользователя", example = "ivan@example.com")
    @NotBlank(message = "Email обязателен")
    @Email(message = "Email должен быть корректным")
    private String email;

    @Schema(description = "Роль пользователя", example = "USER")
    @NotNull(message = "Роль пользователя обязательна")
    private Role role;
}