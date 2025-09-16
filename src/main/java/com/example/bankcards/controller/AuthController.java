package com.example.bankcards.controller;

import com.example.bankcards.dto.security.JwtAuthenticationResponse;
import com.example.bankcards.dto.security.SignInRequest;
import com.example.bankcards.dto.security.SignUpRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Аутентификация", description = "Операции для входа и регистрации пользователей")
public interface AuthController {

    @Operation(summary = "Авторизация пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешная аутентификация",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JwtAuthenticationResponse.class))),
            @ApiResponse(responseCode = "401", description = "Неверные учетные данные",
                    content = @Content),
    })
    JwtAuthenticationResponse singIn(@RequestBody @Valid SignInRequest request);

    @Operation(summary = "Регистрация пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешная регистрация",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JwtAuthenticationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные", content = @Content),
            @ApiResponse(responseCode = "400", description = "Пользователь уже существует", content = @Content)
    })
    JwtAuthenticationResponse signUp(@RequestBody @Valid SignUpRequest request);
}
