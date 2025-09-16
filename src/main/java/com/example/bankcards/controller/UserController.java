package com.example.bankcards.controller;

import com.example.bankcards.dto.UpdatedUserRequest;
import com.example.bankcards.dto.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Управление пользователями")
public interface UserController {

    ResponseEntity<Page<UserDto>> getAllUsers(
            @Parameter(description = "Номер страницы (начиная с 0)", example = "0")
            @RequestParam(defaultValue = "0", required = false) int page,

            @Parameter(description = "Размер страницы", example = "10")
            @RequestParam(defaultValue = "10", required = false) int size,

            @Parameter(description = "Направление сортировки: asc или desc", example = "asc")
            @RequestParam(defaultValue = "asc", required = false) String sortDir
    );

    @Operation(
            summary = "Удаление пользователя",
            description = "Доступно только для ADMIN. Блокирует пользователя по ID.",
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно заблокирован"),
            @ApiResponse(responseCode = "401", description = "Неавторизован", content = @Content),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content)
    })
    ResponseEntity<Void> deleteUser(@Parameter(description = "ID пользователя", example = "2")
                                    @PathVariable Long userId);

    @Operation(
            summary = "Обновить данные пользователя",
            description = "Доступно только для ADMIN. Позволяет обновить username, email или пароль пользователя.",
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно обновлён",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные", content = @Content),
            @ApiResponse(responseCode = "401", description = "Неавторизован", content = @Content),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content)
    })
    ResponseEntity<UserDto> updateUser(@Parameter(description = "ID пользователя", example = "2") @PathVariable Long userId,
                                       @Valid @RequestBody UpdatedUserRequest updatedUserRequest);

    @Operation(
            summary = "Назначить пользователю роль ADMIN",
            description = "Доступно только для ADMIN. Позволяет назначить пользователю роль администратора.",
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Роль ADMIN успешно назначена",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "401", description = "Неавторизован", content = @Content),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content)
    })
    ResponseEntity<UserDto> setAdminRoleToUser(@Parameter(description = "ID пользователя", example = "2") @PathVariable Long userId);
}
