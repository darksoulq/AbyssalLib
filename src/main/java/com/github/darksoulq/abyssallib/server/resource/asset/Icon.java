package com.github.darksoulq.abyssallib.server.resource.asset;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.Map;

/**
 * Represents the {@code icon.png} icon for a resource pack.
 */
public class Icon implements Asset {

    private final byte @NotNull [] data;

    /**
     * Loads {@code resourcepack/pack.png} from the plugin JAR.
     *
     * @param plugin Plugin providing the resource
     */
    public Icon(@NotNull Plugin plugin) {
        try (InputStream in = plugin.getResource("resourcepack/pack.png")) {
            if (in == null) {
                throw new RuntimeException("pack.png not found in plugin JAR at resourcepack/ppack.png");
            }
            this.data = in.readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load pack.png from plugin", e);
        }
    }

    /**
     * Uses manually provided PNG image data.
     *
     * @param data PNG byte array
     */
    public Icon(byte @NotNull [] data) {
        this.data = data;
    }

    @Override
    public void emit(@NotNull Map<String, byte[]> files) {
        files.put("pack.png", data);
    }
}
