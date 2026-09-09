package com.github.darksoulq.abyssallib.common.config.internal;

import com.github.darksoulq.abyssallib.common.config.Config;
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.util.Either;

import java.util.List;
import java.util.Map;

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
            monster = cfg.value("spawn_limits.monster", 70);
            creature = cfg.value("spawn_limits.creature", 10);
            ambient = cfg.value("spawn_limits.ambient", 5);
            waterCreature = cfg.value("spawn_limits.water_creature", 10);
            waterAmbient = cfg.value("spawn_limits.water_ambient", 5);
        }
    }

    public static class ResourcePack {
        public Config.Value<Boolean> enabled;
        public Config.Value<Boolean> compress;
        public Config.Value<SendPhase> sendPhase;
        public Config.Value<String> protocol;
        public Config.Value<String> ip;
        public Config.Value<Integer> port;
        public Config.Value<List<Either<Map<String, Boolean>, String>>> externalPacks;

        public ResourcePack(Config cfg) {
            enabled = cfg.value("resource-pack.enabled", true);
            compress = cfg.value("resource-pack.compress", true);
            sendPhase = cfg.value("resource-pack.send_at", SendPhase.JOIN, Codec.enumCodec(SendPhase.class));
            protocol = cfg.value("resource-pack.protocol", "http");
            ip = cfg.value("resource-pack.ip", "127.0.0.1");
            port = cfg.value("resource-pack.port", 8080);
            externalPacks = cfg.value("resource-pack.external_packs", List.of());
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
            storageType = cfg.value("permissions.storage_type", "sqlite");
            sqlHost = cfg.value("permissions.sql.host", "127.0.0.1");
            sqlPort = cfg.value("permissions.sql.port", 3306);
            sqlDatabase = cfg.value("permissions.sql.database", "abyssallib");
            sqlUsername = cfg.value("permissions.sql.username", "root");
            sqlPassword = cfg.value("permissions.sql.password", "password");
            noSqlUri = cfg.value("permissions.nosql.uri", "mongodb://localhost:27017");
            sqlFile = cfg.value("permissions.local.file", "permissions.db");
            webEnabled = cfg.value("permissions.web.enabled", false);
            webProtocol = cfg.value("permissions.web.protocol", "http");
            webIp = cfg.value("permissions.web.ip", "127.0.0.1");
            webPort = cfg.value("permissions.web.port", 8081);
        }
    }

    public static class Features {
        public Config.Value<Integer> structureBlocksPlacedPerTick;
        public Config.Value<Integer> serverTranslationTickDelay;
        public Config.Value<Boolean> tickServerTranslations;
        public Config.Value<Boolean> enableItemTicking;
        public Config.Value<Boolean> enableEnergyNetwork;
        public Config.Value<Boolean> enableNaturalSpawning;

        public Features(Config cfg) {
            structureBlocksPlacedPerTick = cfg.value("features.structure_blocks_per_tick", 200);
            serverTranslationTickDelay = cfg.value("features.server_translation_delay", 5);
            tickServerTranslations = cfg.value("features.tick_server_translations", true);
            enableItemTicking = cfg.value("features.enable_item_ticking", true);
            enableEnergyNetwork = cfg.value("features.enable_energy_network", true);
            enableNaturalSpawning = cfg.value("features.enable_natural_spawning", false);
        }
    }

    public enum SendPhase {
        CONFIGURATION, JOIN
    }
}