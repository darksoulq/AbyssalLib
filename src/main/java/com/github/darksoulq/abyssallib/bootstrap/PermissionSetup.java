package com.github.darksoulq.abyssallib.bootstrap;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.database.DatabaseLoader;
import com.github.darksoulq.abyssallib.common.util.Try;
import com.github.darksoulq.abyssallib.server.permission.PermissionManager;
import com.github.darksoulq.abyssallib.server.permission.PermissionStorage;
import com.github.darksoulq.abyssallib.server.permission.internal.*;

import java.util.Locale;

public final class PermissionSetup {

    public static void init(AbyssalLib plugin) {
        Try.run(() -> {
            PermissionStorage storage = null;
            String type = AbyssalLib.CONFIG.permissions.storageType.get().toLowerCase(Locale.ROOT);

            storage = switch (type) {
                case "mysql" -> new MysqlPermissionStorage((com.github.darksoulq.abyssallib.common.database.relational.mysql.Database) DatabaseLoader.loadRelational(AbyssalLib.CONFIG.cfg, "permissions", type));
                case "mariadb" -> new MariadbPermissionStorage((com.github.darksoulq.abyssallib.common.database.relational.mariadb.Database) DatabaseLoader.loadRelational(AbyssalLib.CONFIG.cfg, "permissions", type));
                case "postgres", "postgresql" -> new PostgresPermissionStorage((com.github.darksoulq.abyssallib.common.database.relational.postgres.Database) DatabaseLoader.loadRelational(AbyssalLib.CONFIG.cfg, "permissions", type));
                case "h2" -> new H2PermissionStorage((com.github.darksoulq.abyssallib.common.database.relational.h2.Database) DatabaseLoader.loadRelational(AbyssalLib.CONFIG.cfg, "permissions", type));
                case "sqlite" -> new SqlitePermissionStorage((com.github.darksoulq.abyssallib.common.database.relational.sql.Database) DatabaseLoader.loadRelational(AbyssalLib.CONFIG.cfg, "permissions", type));
                case "mongodb" -> new MongoPermissionStorage(DatabaseLoader.loadMongo(AbyssalLib.CONFIG.cfg, "permissions.nosql"));
                case "redis" -> new RedisPermissionStorage(DatabaseLoader.loadRedis(AbyssalLib.CONFIG.cfg, "permissions.nosql"));
                default -> storage;
            };

            if (storage != null) {
                AbyssalLib.PERMISSION_MANAGER = new PermissionManager(plugin, storage);
            } else {
                AbyssalLib.LOGGER.severe("Invalid permission storage type: " + type);
            }

            if (AbyssalLib.CONFIG.permissions.webEnabled.get()) {
                AbyssalLib.PERMISSION_WEB_SERVER = new PermissionWebServer();
                AbyssalLib.PERMISSION_WEB_SERVER.start(
                    AbyssalLib.CONFIG.permissions.webProtocol.get(),
                    AbyssalLib.CONFIG.permissions.webIp.get(),
                    AbyssalLib.CONFIG.permissions.webPort.get()
                );
            }
        }).onFailure(e -> {
            AbyssalLib.LOGGER.severe("Failed to initialize permission storage.");
            e.printStackTrace();
        });
    }
}