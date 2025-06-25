package com.github.darksoulq.abyssallib.server.resource.asset;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.Map;

/**
 * A PNG texture resource emitted to {@code assets/<namespace>/textures/<path>.png}.
 */
@ApiStatus.Experimental
public class Texture implements Asset {

    /** Path relative to {@code textures/}, without extension. */
    private final @NotNull String path;

    /** Namespace (modid). */
    private final @NotNull String namespace;

    /** Texture byte data. */
    private final @NotNull byte[] data;

    /**
     * Loads a texture from {@code resourcepack/<namespace>/textures/<path>.png} inside the plugin JAR.
     *
     * @param plugin    Plugin providing the resource
     * @param namespace The namespace (modid)
     * @param path      Texture path (no extension), relative to {@code textures/}
     */
    public Texture(@NotNull Plugin plugin, @NotNull String namespace, @NotNull String path) {
        this.namespace = namespace;
        this.path = path;

        try (InputStream in = plugin.getResource("resourcepack/" + namespace + "/textures/" + path + ".png")) {
            if (in == null) {
                throw new RuntimeException("Texture not found: " + path);
            }
            this.data = in.readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load texture: " + path, e);
        }
    }

    /**
     * Creates a texture with pre-supplied byte data.
     *
     * @param namespace The namespace (modid)
     * @param path      Texture path (no extension), relative to {@code textures/}
     * @param data      Raw PNG data
     */
    public Texture(@NotNull String namespace, @NotNull String path, @NotNull byte[] data) {
        this.namespace = namespace;
        this.path = path;
        this.data = data;
    }

    /**
     * @return Namespaced texture key in the form {@code namespace:path}
     */
    public @NotNull String file() {
        return namespace + ':' + path;
    }

    /**
     * Emits this texture into the pack output map.
     *
     * @param files File output map
     */
    @Override
    public void emit(@NotNull Map<String, byte[]> files) {
        files.put("assets/" + namespace + "/textures/" + path + ".png", data);
    }
}
