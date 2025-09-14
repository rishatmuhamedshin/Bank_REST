package com.example.bankcards.service.impl;

import com.example.bankcards.dto.UpdatedUserRequest;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.dto.security.UserDTO;
import com.example.bankcards.entity.enumeration.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.exceptions.EmailException;
import com.example.bankcards.exception.exceptions.UserAccessException;
import com.example.bankcards.exception.exceptions.UserNotFoundException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public User create(UserDTO userDTO) {
        if(userRepository.existsByEmail(userDTO.getEmail())){
            throw new EmailException("Пользователь с таким email уже существует");
        }
        if(userRepository.existsByUsername(userDTO.getUsername())){
            throw new UserAccessException("Пользователь с таким именем уже существует");
        }

        log.info("Сохраняем нового user: {}", userDTO.getUsername());
         var user = User.builder()
                 .role(userDTO.getRole())
                 .password(userDTO.getPassword())
                 .email(userDTO.getEmail())
                 .username(userDTO.getUsername())
                 .build();

        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> getAllUsers(Pageable pageable) {
        Page<User> usersPage = userRepository.findAll(pageable);
        return usersPage.map(this::convertToDto);
    }

    @Override
    public void deleteUserById(Long userId) {
        log.info("Удаление пользователя: {}", userId);
        userRepository.deleteById(userId);
    }

    @Override
    public UserDto updateUser(Long userId, UpdatedUserRequest updatedUserRequest) {
        var user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("Пользователь с таким id:" + userId + " не существует")
        );
        user.setEmail(updatedUserRequest.getEmail());
        user.setUsername(updatedUserRequest.getUsername());
        user.setPassword(passwordEncoder.encode(updatedUserRequest.getPassword()));

        var savedUser = userRepository.save(user);

        log.info("Обновленный пользователь {}", userId);
        return convertToDto(savedUser);
    }

    @Override
    @Transactional
    public UserDto setAdminRole(Long userId) {
        var user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("Пользователь с таким id:" + userId + " не существует")
        );
        user.setRole(Role.ROLE_ADMIN);

        var savedUser = userRepository.save(user);

        log.info("Новый админ {}", userId);
        return convertToDto(savedUser);
    }

    //Тут можно использовать MapStruct
    private UserDto convertToDto(User user) {
        return UserDto.builder()
                .username(user.getUsername())
                .role(user.getRole())
                .email(user.getEmail())
                .build();
    }
}
