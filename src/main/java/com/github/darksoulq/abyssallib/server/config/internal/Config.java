package com.github.darksoulq.abyssallib.server.config.internal;

import com.github.darksoulq.abyssallib.server.config.annotation.Comment;
import com.github.darksoulq.abyssallib.server.config.annotation.ConfigFile;
import com.github.darksoulq.abyssallib.server.config.annotation.ConfigProperty;
import com.github.darksoulq.abyssallib.server.config.annotation.Nest;

@ConfigFile(pluginId = "abyssallib")
public class Config {
    @Nest(name = "resource-pack")
    public static class ResourcePack {
        @ConfigProperty(name = "autohost")
        @Comment(comments = "Whether to automatically host the generated resourcepacks")
        public static boolean autoHost = false;

        @ConfigProperty(name = "ip")
        @Comment(comments = "Numerical IP of the server")
        public static String ip = "127.0.0.1";

        @ConfigProperty(name = "port")
        @Comment(comments = "The port to use for the webserver")
        public static int port = 8080;
    }

    @Nest(name = "features")
    public static class Features {
        @ConfigProperty(name = "custom_block_breaking")
        @Comment(comments = "Whether the custom block breaking mechanics should be used")
        public static boolean customBlockBreaking = false;
    }

    @Nest(name = "metrics")
    public static class Metrics {
        @ConfigProperty(name = "enabled")
        @Comment(comments = "Whether metrics (BStats) should be enabled")
        public static boolean enabled = true;
    }
}
