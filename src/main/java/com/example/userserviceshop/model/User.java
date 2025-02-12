package com.example.userserviceshop.model;


import com.example.userserviceshop.utils.SimpleGrantedAuthorityDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String email;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private List<String> roles;

    // Поле для authorities с кастомным десериализатором
    @Transient
    @JsonDeserialize(contentUsing = SimpleGrantedAuthorityDeserializer.class)
    private List<SimpleGrantedAuthority> authorities;

    public User(Long id, String username, String password, String email, List<String> roles) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.roles = roles;
        this.authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public User() {
        // Пустой конструктор для JPA
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Возвращаем authorities, если оно уже заполнено, иначе преобразуем роли
        if (authorities == null) {
            authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password; // Возвращаем пароль из поля
    }

    @Override
    public String getUsername() {
        return this.username; // Возвращаем имя пользователя из поля
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Возвращаем true, если аккаунт не истек
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Возвращаем true, если аккаунт не заблокирован
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Возвращаем true, если учетные данные не истекли
    }

    @Override
    public boolean isEnabled() {
        return true; // Возвращаем true, если аккаунт активен
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;

        // Обновляем authorities при изменении ролей
        this.authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
