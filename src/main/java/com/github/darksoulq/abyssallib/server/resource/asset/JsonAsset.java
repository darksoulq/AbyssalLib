package com.github.darksoulq.abyssallib.server.resource.asset;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.github.darksoulq.abyssallib.util.FileUtils.GSON;

/**
 * Arbitrary JSON file asset with support for dot-path key structure.
 */
public class JsonAsset implements Asset {
    private final @NotNull String namespace;
    private final @NotNull String path;
    private final Map<String, Object> flat = new LinkedHashMap<>();

    private JsonObject preloadedRoot = null;

    /**
     * Creates a new namespaced JSON file.
     *
     * @param namespace The namespace (modid)
     * @param path      Path relative to assets root (e.g. {@code equipment/helmet})
     */
    public JsonAsset(@NotNull String namespace, @NotNull String path) {
        this.namespace = namespace;
        this.path = path;
    }

    /**
     * Loads a JSON file from {@code resourcepack/<namespace>/<path>.json} inside the plugin JAR.
     *
     * @param plugin    The plugin providing the resource
     * @param namespace Namespace of the file
     * @param path      Path relative to {@code assets/<namespace>/}
     */
    public JsonAsset(@NotNull Plugin plugin, @NotNull String namespace, @NotNull String path) {
        this.namespace = namespace;
        this.path = path;
        try (InputStream in = plugin.getResource("resourcepack/" + namespace + "/" + path + ".json")) {
            if (in == null) {
                throw new IllegalArgumentException("Could not find embedded JSON asset: " + namespace + "/" + path);
            }
            this.preloadedRoot = JsonParser.parseReader(new java.io.InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load embedded JSON asset: " + namespace + "/" + path, e);
        }
    }

    /**
     * Adds a value using dot-notation for nested JSON (e.g. {@code foo.bar.baz = 5}).
     *
     * @param key   Dotted path
     * @param value Any serializable value
     * @return this
     */
    public JsonAsset put(@NotNull String key, @NotNull Object value) {
        flat.put(key, value);
        return this;
    }

    @Override
    public void emit(@NotNull Map<String, byte[]> files) {
        JsonObject root = preloadedRoot != null ? preloadedRoot.deepCopy() : new JsonObject();

        for (Map.Entry<String, Object> entry : flat.entrySet()) {
            setNested(root, entry.getKey(), GSON.toJsonTree(entry.getValue()));
        }

        String fullPath = "assets/" + namespace + "/" + path + ".json";
        files.put(fullPath, GSON.toJson(root).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Sets a value deep inside a JsonObject based on dot notation.
     */
    private void setNested(JsonObject root, String dottedKey, JsonElement value) {
        String[] keys = dottedKey.split("\\.");
        JsonObject current = root;

        for (int i = 0; i < keys.length - 1; i++) {
            String key = keys[i];
            if (!current.has(key) || !current.get(key).isJsonObject()) {
                current.add(key, new JsonObject());
            }
            current = current.getAsJsonObject(key);
        }

        current.add(keys[keys.length - 1], value);
    }
}
