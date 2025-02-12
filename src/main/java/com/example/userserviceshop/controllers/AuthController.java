package com.example.userserviceshop.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import com.example.userserviceshop.security.JwtTokenProvider;
import com.example.userserviceshop.services.UserService;
import com.example.userserviceshop.payload.LoginRequest;
import com.example.userserviceshop.model.User;
import com.example.userserviceshop.payload.AuthResponse;
import com.example.userserviceshop.repositories.UserRepository;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager,
                         UserService userService,
                         JwtTokenProvider tokenProvider,
                         UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        AuthResponse response = AuthResponse.builder()
                .token(jwt)
                .tokenType("Bearer")
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        return userService.createUser(user)
                .map(newUser -> {
                    AuthResponse response = AuthResponse.builder()
                            .username(newUser.getUsername())
                            .email(newUser.getEmail())
                            .roles(newUser.getRoles())
                            .build();
                    return ResponseEntity.status(HttpStatus.CREATED).body(response);
                })
                .orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                String username = tokenProvider.getUsernameFromToken(token);
                
                User user = userRepository.findByUsername(username)
                        .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
                
                return ResponseEntity.ok(AuthResponse.builder()
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .roles(user.getRoles())
                        .build());
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Токен не найден"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }
} 