package com.github.darksoulq.abyssallib.common.config.internal;

import com.github.darksoulq.abyssallib.common.config.Config;

import java.util.List;

public class PluginConfig {
    public Config cfg = new Config("abyssallib", "config");
    public Config.Value<Boolean> metrics;
    public ResourcePack rp;
    public SpawnLimits spawnLimits;

    public PluginConfig() {
        metrics = cfg.value("metrics", true)
                .withComment("Whether to enable BStats or not");
        rp = new ResourcePack(cfg);
        spawnLimits = new SpawnLimits(cfg);
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
        public Config.Value<String> ip;
        public Config.Value<Integer> port;

        public Config.Value<List<String>> externalPacks;

        public ResourcePack(Config cfg) {
            enabled = cfg.value("resource-pack.enabled", false)
                    .withComment("Whether autohosting is enabled; in case set to false and ResourcePackManager is installed, RSPM will be used.");
            ip = cfg.value("resource-pack.ip", "127.0.0.1");
            port = cfg.value("resource-pack.port", 8080);
            externalPacks = cfg.value("resource-pack.external_packs", List.of());
        }
    }
}
