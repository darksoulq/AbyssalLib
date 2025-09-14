package com.github.darksoulq.abyssallib.common.config.internal;

import com.github.darksoulq.abyssallib.common.config.Config;

public class PluginConfig {
    public Config cfg = new Config("abyssallib", "config");
    public Config.Value<Boolean> metrics;
    public ResourcePack rp;

    public PluginConfig() {
        metrics = cfg.value("metrics", true)
                .withComment("Whether to enable BStats or not");
        rp = new ResourcePack(cfg);
    }

    public static class ResourcePack {
        public Config.Value<Boolean> enabled;
        public Config.Value<String> ip;
        public Config.Value<Integer> port;

        public ResourcePack(Config cfg) {
            enabled = cfg.value("resource-pack.enabled", false)
                    .withComment("Whether autohosting is enabled; in case set to false and ResourcePackManager is installed, RSPM will be used.");
            ip = cfg.value("resource-pack.ip", "127.0.0.1");
            port = cfg.value("resource-pack.port", 8080);
        }
    }
}
