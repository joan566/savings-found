package com.playground.fondoahorro.infrastructure.adapters;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.sql.DataSource;

import org.sqlite.SQLiteConnection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public final class DatabaseBackup {

    private static final String SQLITE_MAGIC = "SQLite format 3";

    private final DataSource dataSource;
    private final Path databaseFile;

    public DatabaseBackup(DataSource dataSource, @Value("${database.path}") String databasePath) {
        this.dataSource = dataSource;
        this.databaseFile = Path.of(databasePath).toAbsolutePath().normalize();
    }

    public void copyTo(Path destination) throws IOException {
        Path target = destination.toAbsolutePath().normalize();
        ensureDifferentDatabaseFile(target);
        Path directory = target.getParent();
        if (directory != null) {
            Files.createDirectories(directory);
        }

        try (var connection = dataSource.getConnection()) {
            int result = connection.unwrap(SQLiteConnection.class)
                    .getDatabase().backup("main", target.toString(), null);
            if (result != 0) {
                throw new IOException("SQLite no pudo completar la copia de seguridad (código " + result + ").");
            }
        } catch (java.sql.SQLException e) {
            throw new IOException("No fue posible crear la copia de seguridad.", e);
        }
    }

    public void restoreFrom(Path source) throws IOException {
        if (!looksLikeSqliteFile(source)) {
            throw new IOException("El archivo seleccionado no parece ser una copia de seguridad válida.");
        }

        Path backupFile = source.toAbsolutePath().normalize();
        ensureDifferentDatabaseFile(backupFile);
        try (var connection = dataSource.getConnection()) {
            int result = connection.unwrap(SQLiteConnection.class)
                    .getDatabase().restore("main", backupFile.toString(), null);
            if (result != 0) {
                throw new IOException("SQLite no pudo restaurar la copia de seguridad (código " + result + ").");
            }
        } catch (java.sql.SQLException e) {
            throw new IOException("No fue posible restaurar la copia de seguridad.", e);
        }
    }

    private void ensureDifferentDatabaseFile(Path other) throws IOException {
        if (databaseFile.equals(other)
                || (Files.exists(databaseFile) && Files.exists(other) && Files.isSameFile(databaseFile, other))) {
            throw new IOException("La copia de seguridad no puede apuntar al archivo de base de datos activo.");
        }
    }

    private static boolean looksLikeSqliteFile(Path path) throws IOException {
        byte[] header = new byte[16];
        try (InputStream in = Files.newInputStream(path)) {
            if (in.read(header) < 16) {
                return false;
            }
        }
        return new String(header, StandardCharsets.US_ASCII).startsWith(SQLITE_MAGIC);
    }
}
