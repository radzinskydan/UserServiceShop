package com.example.userserviceshop.controllers;

import com.example.userserviceshop.utils.DatabaseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DatabaseController {

    private final DatabaseService databaseService;

    public DatabaseController(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @GetMapping("/database-url")
    public String getDatabaseUrl() {
        return databaseService.getDatabaseUrl();
    }
}
