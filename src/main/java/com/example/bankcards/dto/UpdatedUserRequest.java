package com.example.bankcards.dto;

import com.example.bankcards.util.validation.annoatation.StrongPassword;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Запрос на обновление данных пользователя")
public class UpdatedUserRequest {

    @Schema(description = "Имя пользователя", example = "ivan_ivanov")
    @NotBlank(message = "Имя пользователя обязательно")
    @Size(min = 3, max = 50, message = "Имя пользователя должно быть от 3 до 50 символов")
    private String username;

    @Schema(description = "Email пользователя", example = "ivan@example.com")
    @NotBlank(message = "Email обязателен")
    @Email(message = "Email должен быть корректным")
    private String email;

    @Schema(description = "Пароль пользователя", example = "StrongPass123!")
    @Size(min = 8, max = 255, message = "Пароль должен быть от 8 до 255 символов")
    @StrongPassword
    private String password;
}