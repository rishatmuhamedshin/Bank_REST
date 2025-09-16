package com.example.bankcards.controller.impl;

import com.example.bankcards.controller.AuthController;
import com.example.bankcards.dto.security.JwtAuthenticationResponse;
import com.example.bankcards.dto.security.SignInRequest;
import com.example.bankcards.dto.security.SignUpRequest;
import com.example.bankcards.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST-контроллер для управления аутентификацией пользователей.
 * <p>
 * Содержит эндпоинты для авторизации и регистрации пользователей.
 * Использует {@link AuthenticationService} для выполнения бизнес-логики.
 * </p>
 *
 * <p><b>Маршрут:</b> {@code /api/auth}</p>
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthControllerImpl implements AuthController {

    private final AuthenticationService authenticationService;

    /**
     * Авторизация (вход) пользователя.
     *
     * @param request DTO с логином и паролем пользователя
     * @return {@link JwtAuthenticationResponse} с JWT-токеном при успешной аутентификации
     *
     * @apiNote Возвращает статус 200 при успешной аутентификации и 401 при неверных учетных данных.
     */
    @PostMapping("/signin")
    public JwtAuthenticationResponse singIn(@RequestBody @Valid SignInRequest request) {
        return authenticationService.signIn(request);
    }

    /**
     * Регистрация нового пользователя.
     *
     * @param request DTO с регистрационными данными (логин, email, пароль и др.)
     * @return {@link JwtAuthenticationResponse} с JWT-токеном при успешной регистрации
     *
     * @apiNote Возвращает статус 200 при успешной регистрации,
     * 400 при некорректных данных или если пользователь уже существует.
     */
    @PostMapping("/signup")
    public JwtAuthenticationResponse signUp(@RequestBody @Valid SignUpRequest request) {
        return authenticationService.signUp(request);
    }
}