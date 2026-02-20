package com.github.darksoulq.abyssallib.common.database;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.config.Config;
import com.github.darksoulq.abyssallib.common.database.relational.AbstractDatabase;

import java.io.File;
import java.util.Locale;

/**
 * A factory utility class responsible for loading and initializing database instances.
 * <p>
 * This class maps configuration keys to specific database implementations for
 * Relational (SQL), MongoDB, and Redis backends.
 */
public class DatabaseLoader {

    /**
     * Initializes and connects to a relational database based on the provided configuration.
     *
     * @param config   The {@link Config} instance containing database settings.
     * @param basePath The root path in the configuration where SQL settings are stored.
     * @param type     The type of database to load (e.g., "mysql", "sqlite", "postgres").
     * @return An initialized {@link AbstractDatabase} instance, or {@code null} if the type is unknown.
     * @throws Exception If a connection error occurs during initialization.
     */
    public static AbstractDatabase loadRelational(Config config, String basePath, String type) throws Exception {
        return switch (type.toLowerCase(Locale.ROOT)) {
            case "mysql" -> {
                com.github.darksoulq.abyssallib.common.database.relational.mysql.Database db = new com.github.darksoulq.abyssallib.common.database.relational.mysql.Database(
                    config.value(basePath + ".sql.host", "127.0.0.1").get(),
                    config.value(basePath + ".sql.port", 3306).get(),
                    config.value(basePath + ".sql.database", "abyssallib").get(),
                    config.value(basePath + ".sql.username", "root").get(),
                    config.value(basePath + ".sql.password", "password").get()
                );
                db.connect();
                yield db;
            }
            case "mariadb" -> {
                com.github.darksoulq.abyssallib.common.database.relational.mariadb.Database db = new com.github.darksoulq.abyssallib.common.database.relational.mariadb.Database(
                    config.value(basePath + ".sql.host", "127.0.0.1").get(),
                    config.value(basePath + ".sql.port", 3306).get(),
                    config.value(basePath + ".sql.database", "abyssallib").get(),
                    config.value(basePath + ".sql.username", "root").get(),
                    config.value(basePath + ".sql.password", "password").get()
                );
                db.connect();
                yield db;
            }
            case "postgres", "postgresql" -> {
                com.github.darksoulq.abyssallib.common.database.relational.postgres.Database db = new com.github.darksoulq.abyssallib.common.database.relational.postgres.Database(
                    config.value(basePath + ".sql.host", "127.0.0.1").get(),
                    config.value(basePath + ".sql.port", 5432).get(),
                    config.value(basePath + ".sql.database", "abyssallib").get(),
                    config.value(basePath + ".sql.username", "postgres").get(),
                    config.value(basePath + ".sql.password", "password").get()
                );
                db.connect();
                yield db;
            }
            case "sqlite" -> {
                com.github.darksoulq.abyssallib.common.database.relational.sql.Database db = new com.github.darksoulq.abyssallib.common.database.relational.sql.Database(
                    new File(AbyssalLib.getInstance().getDataFolder(), config.value(basePath + ".local.file", "permissions.db").get())
                );
                db.connect();
                yield db;
            }
            case "h2" -> {
                com.github.darksoulq.abyssallib.common.database.relational.h2.Database db = new com.github.darksoulq.abyssallib.common.database.relational.h2.Database(
                    new File(AbyssalLib.getInstance().getDataFolder(), config.value(basePath + ".local.file", "permissions").get().replace(".db", ""))
                );
                db.connect();
                yield db;
            }
            default -> null;
        };
    }

    /**
     * Initializes and connects to a MongoDB database.
     *
     * @param config   The configuration instance.
     * @param basePath The configuration path where Mongo settings are stored.
     * @return An initialized MongoDB {@link com.github.darksoulq.abyssallib.common.database.nosql.mongodb.Database},
     * or {@code null} if the URI is disabled or empty.
     */
    public static com.github.darksoulq.abyssallib.common.database.nosql.mongodb.Database loadMongo(Config config, String basePath) {
        String uri = config.value(basePath + ".uri", "mongodb://localhost:27017").get();
        if (uri == null || uri.equalsIgnoreCase("none") || uri.isEmpty()) return null;

        com.github.darksoulq.abyssallib.common.database.nosql.mongodb.Database db = new com.github.darksoulq.abyssallib.common.database.nosql.mongodb.Database(
            uri,
            config.value(basePath + ".database", "abyssallib").get()
        );
        db.connect();
        return db;
    }

    /**
     * Initializes and connects to a Redis database.
     *
     * @param config   The configuration instance.
     * @param basePath The configuration path where Redis settings are stored.
     * @return An initialized Redis {@link com.github.darksoulq.abyssallib.common.database.nosql.redis.Database},
     * or {@code null} if the host is disabled or empty.
     */
    public static com.github.darksoulq.abyssallib.common.database.nosql.redis.Database loadRedis(Config config, String basePath) {
        String host = config.value(basePath + ".uri", "127.0.0.1").get();
        if (host == null || host.equalsIgnoreCase("none") || host.isEmpty()) return null;

        com.github.darksoulq.abyssallib.common.database.nosql.redis.Database db = new com.github.darksoulq.abyssallib.common.database.nosql.redis.Database(
            host,
            config.value(basePath + ".port", 6379).get(),
            config.value(basePath + ".password", "").get()
        );
        db.connect();
        return db;
    }
}