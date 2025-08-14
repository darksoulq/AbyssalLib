package com.github.darksoulq.abyssallib.server.config.internal;

import com.github.darksoulq.abyssallib.server.config.annotation.Comment;
import com.github.darksoulq.abyssallib.server.config.annotation.ConfigFile;
import com.github.darksoulq.abyssallib.server.config.annotation.ConfigProperty;
import com.github.darksoulq.abyssallib.server.config.annotation.Nest;

@ConfigFile(id = "abyssallib")
public class Config {
    @Nest("resource-pack")
    public static class ResourcePack {
        @ConfigProperty("autohost")
        @Comment("Whether to automatically host the generated resourcepacks")
        public static boolean autoHost = false;

        @ConfigProperty("ip")
        @Comment("Numerical IP of the server")
        public static String ip = "127.0.0.1";

        @ConfigProperty("port")
        @Comment("The port to use for the webserver")
        public static int port = 8080;
    }

    @Nest("features")
    public static class Features {
        @ConfigProperty("custom_block_breaking")
        @Comment("Whether the custom block breaking mechanics should be used")
        public static boolean customBlockBreaking = false;
    }

    @Nest("metrics")
    public static class Metrics {
        @ConfigProperty("enabled")
        @Comment("Whether metrics (BStats) should be enabled")
        public static boolean enabled = true;
    }
}
