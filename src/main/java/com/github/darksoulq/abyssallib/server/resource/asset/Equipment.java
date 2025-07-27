package com.github.darksoulq.abyssallib.server.resource.asset;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.github.darksoulq.abyssallib.util.FileUtils.GSON;

/**
 * Represents a resource pack equipment JSON: `assets/<namespace>/equipment/<id>.json`.
 */
public class Equipment implements Asset {
    private final @NotNull String namespace;
    private final @NotNull String id;
    private final byte[] rawData;

    private final Map<String, List<LayerEntry>> layers = new LinkedHashMap<>();

    /**
     * Autoload existing equipment JSON from plugin JAR (resourcepack/<namespace>/equipment/<id>.json).
     */
    public Equipment(@NotNull Plugin plugin, @NotNull String namespace, @NotNull String id) {
        this.namespace = namespace;
        this.id = id;
        byte[] data;
        try (InputStream in = plugin.getResource("resourcepack/" + namespace + "/equipment/" + id + ".json")) {
            if (in == null) throw new IllegalStateException("Equipment file not found");
            data = in.readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load equipment JSON", e);
        }
        this.rawData = data;
    }

    /**
     * Create empty equipment JSON programmatically.
     */
    public Equipment(@NotNull String namespace, @NotNull String id) {
        this.namespace = namespace;
        this.id = id;
        this.rawData = null;
    }

    /**
     * Adds a layer entry under the specified entity layer (e.g. "humanoid", "wings").
     */
    public Equipment addLayer(@NotNull String layerType, @NotNull LayerEntry entry) {
        layers.computeIfAbsent(layerType, k -> new ArrayList<>()).add(entry);
        return this;
    }

    @Override
    public void emit(@NotNull Map<String, byte[]> files) {
        if (rawData != null) {
            files.put(path(), rawData);
            return;
        }

        JsonObject root = new JsonObject();
        JsonObject layersObj = new JsonObject();

        for (var entry : layers.entrySet()) {
            JsonArray arr = new JsonArray();
            for (LayerEntry le : entry.getValue()) {
                JsonObject obj = new JsonObject();
                obj.addProperty("texture", le.texture());
                if (le.usePlayerTexture != null) obj.addProperty("use_player_texture", le.usePlayerTexture);
                if (le.dyeable != null) {
                    JsonObject dye = new JsonObject();
                    if (le.dyeable.colorWhenUndyed != null)
                        dye.addProperty("color_when_undyed", le.dyeable.colorWhenUndyed);
                    obj.add("dyeable", dye);
                }
                arr.add(obj);
            }
            layersObj.add(entry.getKey(), arr);
        }

        root.add("layers", layersObj);
        files.put(path(), GSON.toJson(root).getBytes(StandardCharsets.UTF_8));
    }

    private String path() {
        return "assets/" + namespace + "/equipment/" + id + ".json";
    }

    public static class LayerEntry {
        private final Texture texture;
        private Boolean usePlayerTexture;
        private Dyeable dyeable;

        public LayerEntry(@NotNull Texture texture) {
            this.texture = texture;
        }

        public LayerEntry usePlayerTexture(boolean b) {
            this.usePlayerTexture = b;
            return this;
        }

        public LayerEntry dyeable(Integer colorWhenUndyed) {
            this.dyeable = new Dyeable(colorWhenUndyed);
            return this;
        }

        public String texture() { return texture.file(); }
    }

    private static class Dyeable {
        final Integer colorWhenUndyed;
        Dyeable(Integer c) { this.colorWhenUndyed = c; }
    }
}
