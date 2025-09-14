package com.example.bankcards.dto.security;

import com.example.bankcards.entity.enumeration.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {
    private String username;
    private String email;
    private String password;
    private Role role;
}
