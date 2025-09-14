package com.example.bankcards.dto.security;

import com.example.bankcards.util.validation.annoatation.StrongPassword;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Запрос на аутентификацию")
public class SignInRequest {

    @Schema(description = "Имя пользователя", example = "ADMIN ADMINOV")
    @NotBlank(message = "Имя пользователя обязательно")
    @Size(min = 5,max = 50, message = "Имя в пределах от 5 до 50 символов")
    private String username;

    @Schema(description = "Пароль", example = "adminAdminov123$")
    @StrongPassword
    @NotBlank(message = "Пароль не должен быть пустым")
    @Size(min = 8,max = 255, message = "Пароль в пределах от 8 до 255 символов")
    private String password;
}

