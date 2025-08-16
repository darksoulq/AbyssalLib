package com.github.darksoulq.abyssallib.server.config.internal;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable()
public class Config {
    @Setting("metrics")
    @Comment("Whether metrics should be enabled")
    public boolean metrics = true;

    @Setting("resource_pack")
    @Comment("Settings related to ResourcePafk hosting")
    public ResourcePack resourcePack = new ResourcePack();

    @Setting("features")
    @Comment("Settings related to features added by library")
    public Features features = new Features();

    @ConfigSerializable
    public static class ResourcePack {
        @Setting("enabled")
        @Comment("Whether autohosting is enabled; in case set to false and ResourcePackManager is installed, RSPM will be used.")
        public boolean enabled = false;
        @Setting("ip")
        public String ip = "127.0.0.1";
        @Setting("port")
        public int port = 8080;
    }
    @ConfigSerializable
    public static class Features {
        @Setting("custom_block_breaking")
        @Comment("EXTREMELY BUGGY!")
        public boolean customBlockBreaking = false;
    }
}
