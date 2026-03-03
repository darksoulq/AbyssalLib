package com.github.darksoulq.abyssallib.common.database;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.config.Config;
import com.github.darksoulq.abyssallib.common.database.relational.AbstractDatabase;

import java.io.File;
import java.sql.SQLException;
import java.util.Locale;

/**
 * A factory utility class responsible for loading and initializing database instances.
 * <p>
 * This class maps configuration keys to specific database implementations for
 * Relational (SQL), MongoDB, and Redis backends.
 */
public class DatabaseLoader {

    /**
     * A record representing the credentials required to connect to a relational database.
     *
     * @param host     The database host address.
     * @param port     The database port.
     * @param database The name of the database.
     * @param username The database username.
     * @param password The database password.
     */
    private record RelationalCredentials(String host, int port, String database, String username, String password) {}

    /**
     * Retrieves relational database credentials from the provided configuration.
     *
     * @param config      The configuration instance to read from.
     * @param basePath    The base path in the configuration where the SQL settings are stored.
     * @param defaultPort The default port to use if not specified in the configuration.
     * @param defaultUser The default username to use if not specified in the configuration.
     * @return A {@link RelationalCredentials} object containing the connection details.
     */
    private static RelationalCredentials getCredentials(Config config, String basePath, int defaultPort, String defaultUser) {
        return new RelationalCredentials(
            config.value(basePath + ".sql.host", "127.0.0.1").get(),
            config.value(basePath + ".sql.port", defaultPort).get(),
            config.value(basePath + ".sql.database", "abyssallib").get(),
            config.value(basePath + ".sql.username", defaultUser).get(),
            config.value(basePath + ".sql.password", "password").get()
        );
    }

    /**
     * Initializes and connects to a relational database based on the provided configuration.
     * <p>
     * Supported types include: {@code mysql}, {@code mariadb}, {@code postgres}/{@code postgresql}, {@code sqlite}, and {@code h2}.
     *
     * @param config   The {@link Config} instance containing database settings.
     * @param basePath The root path in the configuration where SQL settings are stored.
     * @param type     The type of database to load (e.g., "mysql", "sqlite", "postgres").
     * @return An initialized and connected {@link AbstractDatabase} instance.
     * @throws SQLException If a connection error occurs during initialization or if the database type is unknown.
     */
    public static AbstractDatabase loadRelational(Config config, String basePath, String type) throws SQLException {
        try {
            return switch (type.toLowerCase(Locale.ROOT)) {
                case "mysql" -> {
                    RelationalCredentials creds = getCredentials(config, basePath, 3306, "root");
                    var db = new com.github.darksoulq.abyssallib.common.database.relational.mysql.Database(
                        creds.host(), creds.port(), creds.database(), creds.username(), creds.password()
                    );
                    db.connect();
                    yield db;
                }
                case "mariadb" -> {
                    RelationalCredentials creds = getCredentials(config, basePath, 3306, "root");
                    var db = new com.github.darksoulq.abyssallib.common.database.relational.mariadb.Database(
                        creds.host(), creds.port(), creds.database(), creds.username(), creds.password()
                    );
                    db.connect();
                    yield db;
                }
                case "postgres", "postgresql" -> {
                    RelationalCredentials creds = getCredentials(config, basePath, 5432, "postgres");
                    var db = new com.github.darksoulq.abyssallib.common.database.relational.postgres.Database(
                        creds.host(), creds.port(), creds.database(), creds.username(), creds.password()
                    );
                    db.connect();
                    yield db;
                }
                case "sqlite" -> {
                    var db = new com.github.darksoulq.abyssallib.common.database.relational.sql.Database(
                        new File(AbyssalLib.getInstance().getDataFolder(), config.value(basePath + ".local.file", "permissions.db").get())
                    );
                    db.connect();
                    yield db;
                }
                case "h2" -> {
                    var db = new com.github.darksoulq.abyssallib.common.database.relational.h2.Database(
                        new File(AbyssalLib.getInstance().getDataFolder(), config.value(basePath + ".local.file", "permissions").get().replace(".db", ""))
                    );
                    db.connect();
                    yield db;
                }
                default -> throw new IllegalArgumentException("Unknown database type specified: " + type);
            };
        } catch (Exception e) {
            throw new SQLException("Failed to initialize relational database connection for type: " + type, e);
        }
    }

    /**
     * Initializes and connects to a MongoDB database.
     *
     * @param config   The {@link Config} instance containing MongoDB settings.
     * @param basePath The configuration path where Mongo settings are stored.
     * @return An initialized MongoDB {@link com.github.darksoulq.abyssallib.common.database.nosql.mongodb.Database},
     * or {@code null} if the URI is disabled (e.g., "none") or empty.
     */
    public static com.github.darksoulq.abyssallib.common.database.nosql.mongodb.Database loadMongo(Config config, String basePath) {
        String uri = config.value(basePath + ".uri", "mongodb://localhost:27017").get();
        if (uri == null || uri.equalsIgnoreCase("none") || uri.isEmpty()) return null;

        var db = new com.github.darksoulq.abyssallib.common.database.nosql.mongodb.Database(
            uri,
            config.value(basePath + ".database", "abyssallib").get()
        );
        db.connect();
        return db;
    }

    /**
     * Initializes and connects to a Redis database.
     *
     * @param config   The {@link Config} instance containing Redis settings.
     * @param basePath The configuration path where Redis settings are stored.
     * @return An initialized Redis {@link com.github.darksoulq.abyssallib.common.database.nosql.redis.Database},
     * or {@code null} if the host is disabled (e.g., "none") or empty.
     */
    public static com.github.darksoulq.abyssallib.common.database.nosql.redis.Database loadRedis(Config config, String basePath) {
        String host = config.value(basePath + ".uri", "127.0.0.1").get();
        if (host == null || host.equalsIgnoreCase("none") || host.isEmpty()) return null;

        var db = new com.github.darksoulq.abyssallib.common.database.nosql.redis.Database(
            host,
            config.value(basePath + ".port", 6379).get(),
            config.value(basePath + ".password", "").get()
        );
        db.connect();
        return db;
    }
}