package com.example.bankcards.dto.security;

import com.example.bankcards.util.validation.annoatation.StrongPassword;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Запрос на регистрацию")
public class SignUpRequest {

    @Schema(description = "Имя пользователя", example = "Rishat432")
    @NotBlank(message = "Имя пользователя обязательно")
    @Size(min = 5,max = 50, message = "Имя в пределах от 5 до 50 символов")
    private String username;

    @Schema(description = "Адрес электронной почты", example = "Rishat@example.com")
    @Size(min = 5,max = 255, message = "Электронная почта должна иметь от 5 до 255 символов")
    @NotBlank(message = "Почта не может быть пустой")
    @Email
    private String email;

    @Schema(description = "Пароль", example = "mySuperPuperParol12314%$")
    @StrongPassword
    @NotBlank(message = "Пароль не должен быть пустым")
    @Size(min = 8,max = 255, message = "Пароль в пределах от 8 до 255 символов")
    private String password;

}