package com.example.userserviceshop.controllers;

import com.example.userserviceshop.model.User;
import com.example.userserviceshop.services.UserService;
import com.example.userserviceshop.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/users")
@Tag(name = "Администрирование", description = "API для управления пользователями (только для админов)")
public class AdminController {
    private final UserService userService;
    private final JwtTokenProvider tokenProvider;

    @Autowired
    public AdminController(UserService userService, JwtTokenProvider tokenProvider) {
        this.userService = userService;
        this.tokenProvider = tokenProvider;
    }

    @Operation(summary = "Создать нового пользователя")
    @SecurityRequirement(name = "bearer-token")
    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody User user,
                                      @RequestHeader("Authorization") String tokenHeader) {
        try {
            validateAdminToken(tokenHeader);
            Optional<User> registeredUser = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Создать нового администратора")
    @SecurityRequirement(name = "bearer-token")
    @PostMapping("/create/admin")
    public ResponseEntity<?> createAdmin(@RequestBody User user,
                                       @RequestHeader("Authorization") String tokenHeader) {
        try {
            validateAdminToken(tokenHeader);
            Optional<User> registeredAdmin = userService.createAdmin(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(registeredAdmin);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Получить список всех пользователей")
    @SecurityRequirement(name = "bearer-token")
    @GetMapping("/getAll")
    public ResponseEntity<?> getUsers(@RequestHeader("Authorization") String tokenHeader) {
        try {
            validateAdminToken(tokenHeader);
            List<User> allUsers = userService.getAllUsers();
            return ResponseEntity.ok(allUsers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    private void validateAdminToken(String tokenHeader) {
        if (tokenHeader == null || !tokenHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Неверный формат токена");
        }

        String token = tokenHeader.substring(7);
        String username = tokenProvider.getUsernameFromToken(token);
        User admin = userService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        if (!admin.getRoles().contains("ROLE_ADMIN")) {
            throw new RuntimeException("Доступ запрещен. Требуются права администратора");
        }
    }
}
