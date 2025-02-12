package com.example.userserviceshop;

import org.springframework.boot.SpringApplication;

public class TestUserServiceShopApplication {

    public static void main(String[] args) {
        SpringApplication.from(UserServiceShopApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
