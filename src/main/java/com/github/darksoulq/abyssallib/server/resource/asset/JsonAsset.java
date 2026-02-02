package com.github.darksoulq.abyssallib.server.resource.asset;

import com.google.gson.*;
import org.bukkit.plugin.Plugin;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class JsonAsset implements Asset {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final String namespace;
    private final String path;
    private final JsonObject root;

    public JsonAsset(String namespace, String path) {
        this.namespace = namespace;
        this.path = path;
        this.root = new JsonObject();
    }

    public JsonAsset(Plugin plugin, String namespace, String path) {
        this.namespace = namespace;
        this.path = path;
        String resourcePath = "resourcepack/" + namespace + "/" + path + ".json";
        try (InputStream in = plugin.getResource(resourcePath)) {
            if (in == null) {
                throw new IllegalArgumentException("Asset not found: " + resourcePath);
            }
            this.root = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load JSON asset: " + resourcePath, e);
        }
    }

    public JsonAsset put(String key, Object value) {
        setNested(root, key, GSON.toJsonTree(value));
        return this;
    }

    @Override
    public void emit(Map<String, byte[]> files) {
        String fullPath = "assets/" + namespace + "/" + path + ".json";
        files.put(fullPath, GSON.toJson(root).getBytes(StandardCharsets.UTF_8));
    }

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