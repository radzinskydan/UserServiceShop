package com.example.userserviceshop.services;

import com.example.userserviceshop.exceptions.EmailAlreadyExistsException;
import com.example.userserviceshop.exceptions.UserAlreadyExistsException;
import com.example.userserviceshop.model.User;
import com.example.userserviceshop.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> createUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Пользователь с таким именем уже существует.");
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Пользователь с такой электронной почтой уже существует.");
        }

        return Optional.of(createUserWithRole(user, "ROLE_USER"));
    }

    public Optional<User> createAdmin(User user) {
//        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
//            throw new UserAlreadyExistsException("Пользователь с таким именем уже существует.");
//        }
//
//        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
//            throw new EmailAlreadyExistsException("Пользователь с такой электронной почтой уже существует.");
//        }

        return Optional.of(createUserWithRole(user, "ROLE_ADMIN"));
    }

    private User createUserWithRole(User user, String role) {
        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        newUser.setEmail(user.getEmail());
        List<String> roles = Collections.singletonList(role);
        newUser.setRoles(roles);
        
        return userRepository.save(newUser);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }


    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

}
