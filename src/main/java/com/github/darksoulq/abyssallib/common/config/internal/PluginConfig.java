package com.github.darksoulq.abyssallib.common.config.internal;

import com.github.darksoulq.abyssallib.common.config.Config;
import com.github.darksoulq.abyssallib.common.serialization.Codec;

import java.util.List;

public class PluginConfig {
    public Config cfg = new Config("abyssallib", "config").schema(1).apply();
    public Config.Value<Boolean> metrics;
    public ResourcePack rp;
    public SpawnLimits spawnLimits;
    public Features features;
    public Permissions permissions;

    public PluginConfig() {
        metrics = cfg.value("metrics", true);
        rp = new ResourcePack(cfg);
        spawnLimits = new SpawnLimits(cfg);
        features = new Features(cfg);
        permissions = new Permissions(cfg);
    }

    public static class SpawnLimits {
        public Config.Value<Integer> monster;
        public Config.Value<Integer> creature;
        public Config.Value<Integer> ambient;
        public Config.Value<Integer> waterCreature;
        public Config.Value<Integer> waterAmbient;

        public SpawnLimits(Config cfg) {
            cfg.addComment("spawn_limits", "Adjust entity spawn limits per world category.");
            monster = cfg.value("spawn_limits.monster", 70);
            creature = cfg.value("spawn_limits.creature", 10);
            ambient = cfg.value("spawn_limits.ambient", 5);
            waterCreature = cfg.value("spawn_limits.water_creature", 10);
            waterAmbient = cfg.value("spawn_limits.water_ambient", 5);
        }
    }

    public static class ResourcePack {
        public Config.Value<Boolean> enabled;
        public Config.Value<SendPhase> sendPhase;
        public Config.Value<String> protocol;
        public Config.Value<String> ip;
        public Config.Value<Integer> port;
        public Config.Value<List<String>> externalPacks;

        public ResourcePack(Config cfg) {
            enabled = cfg.value("resource-pack.enabled", true)
                .withComment("Whether autohosting is enabled; in case set to false and ResourcePackManager is installed, RSPM will be used.");
            sendPhase = cfg.value("resource-pack.send_at", SendPhase.JOIN, Codec.enumCodec(SendPhase.class))
                .withComment("When to send the resource-pack (either CONFIGURATION or JOIN)");
            protocol = cfg.value("resource-pack.protocol", "http")
                .withComment("The protocol to use for the resource pack server (http or https).");
            ip = cfg.value("resource-pack.ip", "127.0.0.1")
                .withComment("The IP address to bind the resource pack server to.");
            port = cfg.value("resource-pack.port", 8080)
                .withComment("The port to bind the resource pack server to.");
            externalPacks = cfg.value("resource-pack.external_packs", List.of());
            externalPacks.withComment("List of external resource pack paths to include.");
        }
    }

    public static class Permissions {
        public Config.Value<String> storageType;
        public Config.Value<String> sqlHost;
        public Config.Value<Integer> sqlPort;
        public Config.Value<String> sqlDatabase;
        public Config.Value<String> sqlUsername;
        public Config.Value<String> sqlPassword;
        public Config.Value<String> noSqlUri;
        public Config.Value<String> sqlFile;

        public Config.Value<Boolean> webEnabled;
        public Config.Value<String> webProtocol;
        public Config.Value<String> webIp;
        public Config.Value<Integer> webPort;

        public Permissions(Config cfg) {
            storageType = cfg.value("permissions.storage_type", "sqlite")
                .withComment("Storage type for permissions. Valid options: 'sqlite', 'mysql', 'mariadb', 'postgres', 'h2', 'mongodb', 'redis'");

            cfg.addComment("permissions.sql", "SQL Database Settings (Used for mysql, mariadb, postgres)");
            sqlHost = cfg.value("permissions.sql.host", "127.0.0.1");
            sqlPort = cfg.value("permissions.sql.port", 3306);
            sqlDatabase = cfg.value("permissions.sql.database", "abyssallib");
            sqlUsername = cfg.value("permissions.sql.username", "root");
            sqlPassword = cfg.value("permissions.sql.password", "password");

            cfg.addComment("permissions.nosql", "NoSQL Database Settings (Used for mongodb, redis)");
            noSqlUri = cfg.value("permissions.nosql.uri", "mongodb://localhost:27017")
                .withComment("URI for MongoDB or host for Redis");

            cfg.addComment("permissions.local", "Local Database Settings (Used for sqlite, h2)");
            sqlFile = cfg.value("permissions.local.file", "permissions.db")
                .withComment("File name for local databases");

            cfg.addComment("permissions.web", "Web Editor Settings");
            webEnabled = cfg.value("permissions.web.enabled", false)
                .withComment("Enable the web editor server.");
            webProtocol = cfg.value("permissions.web.protocol", "http")
                .withComment("The protocol to use for the web editor (http or https).");
            webIp = cfg.value("permissions.web.ip", "127.0.0.1")
                .withComment("The IP address to bind the web editor to.");
            webPort = cfg.value("permissions.web.port", 8081)
                .withComment("The port to bind the web editor to.");
        }
    }

    public static class Features {
        public Config.Value<Integer> structureBlocksPlacedPerTick;
        public Config.Value<Integer> serverTranslationTickDelay;
        public Config.Value<Boolean> tickServerTranslations;
        public Config.Value<Boolean> enableItemTicking;
        public Config.Value<Boolean> enableEnergyNetwork;

        public Features(Config cfg) {
            structureBlocksPlacedPerTick = cfg.value("features.structure_blocks_per_tick", 200)
                .withComment("How many blocks should be placed per tick when using structure block");
            serverTranslationTickDelay = cfg.value("features.server_translation_delay", 5)
                .withComment("How frequently server translations should update", "Lower values may cause ghost items more frequently");
            tickServerTranslations = cfg.value("features.tick_server_translations", true)
                .withComment("Whether or not server side translations should be updated", "This is useful for item tooltips that need to update values each tick");
            enableItemTicking = cfg.value("features.enable_item_ticking", true)
                .withComment("Whether the Item#onTick method should be called, Disabling can increase performance", "DO NOT disable if a plugin is using it");
            enableEnergyNetwork = cfg.value("features.enable_energy_network", true)
                .withComment("Whether the Energy API should be enabled", "DO NOT disable it if using a plugin that depends on it");
        }
    }

    public enum SendPhase {
        CONFIGURATION, JOIN
    }
}