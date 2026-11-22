package com.playground.fondoahorro.infrastructure.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.flyway.autoconfigure.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
public class DatabaseConfiguration {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConfiguration.class);

    @Bean
    public FlywayMigrationStrategy prepareDatabaseDirectory(@Value("${database.path}") String databasePath) {
        return flyway -> {
            Path file = Path.of(databasePath);
            Path directory = file.toAbsolutePath().getParent();
            try {
                if (directory != null) {
                    Files.createDirectories(directory);
                }
            } catch (IOException e) {
                throw new IllegalStateException("No fue posible crear la carpeta de datos: " + directory, e);
            }

            log.info("Inicializando base de datos en {}", file.toAbsolutePath());
            flyway.migrate();
        };
    }
}
