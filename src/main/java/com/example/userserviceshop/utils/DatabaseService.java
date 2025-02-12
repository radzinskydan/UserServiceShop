package com.example.userserviceshop.utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import javax.sql.DataSource;

@Service
public class DatabaseService {

    @PersistenceContext
    private EntityManager entityManager;

    private final DataSource dataSource;

    public DatabaseService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String getDatabaseUrl() {
        try {
            return dataSource.getConnection().getMetaData().getURL();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

