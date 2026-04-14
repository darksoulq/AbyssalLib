package com.github.darksoulq.abyssallib.server.resource.asset;

import com.github.darksoulq.abyssallib.server.resource.asset.definition.AtlasSource;
import com.google.gson.*;
import org.bukkit.plugin.Plugin;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Atlas implements Asset {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final String namespace;
    private final String id;
    private final List<JsonElement> sources = new ArrayList<>();

    public Atlas(Plugin plugin, String namespace, String id) {
        this.namespace = namespace;
        this.id = id;
        String path = "resourcepack/" + namespace + "/atlases/" + id + ".json";
        try (InputStream in = plugin.getResource(path)) {
            if (in != null) {
                String json = new String(in.readAllBytes(), StandardCharsets.UTF_8);
                JsonObject root = JsonParser.parseString(json).getAsJsonObject();
                if (root.has("sources")) {
                    root.getAsJsonArray("sources").forEach(sources::add);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load atlas: " + path, e);
        }
    }

    public Atlas(String namespace, String id) {
        this.namespace = namespace;
        this.id = id;
    }

    public Atlas(String namespace, String id, byte[] data) {
        this.namespace = namespace;
        this.id = id;
        try {
            String json = new String(data, StandardCharsets.UTF_8);
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            if (root.has("sources")) {
                root.getAsJsonArray("sources").forEach(sources::add);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse atlas data for: " + id, e);
        }
    }

    public String getId() {
        return id;
    }

    public Atlas addSource(AtlasSource source) {
        JsonObject newSource = source.toJson();
        if (!newSource.has("type")) return this;

        String newType = newSource.get("type").getAsString();

        for (JsonElement src : sources) {
            if (!src.isJsonObject()) continue;
            JsonObject obj = src.getAsJsonObject();
            if (!obj.has("type") || !obj.get("type").getAsString().equals(newType)) continue;

            if (newType.equals("directory") && obj.has("source") && newSource.has("source")) {
                if (obj.get("source").getAsString().equals(newSource.get("source").getAsString())) return this;
            } else if (newType.equals("single") && obj.has("resource") && newSource.has("resource")) {
                if (obj.get("resource").getAsString().equals(newSource.get("resource").getAsString())) return this;
            } else if (newType.equals("paletted_permutations") && obj.has("palette_key") && newSource.has("palette_key")) {
                if (obj.get("palette_key").getAsString().equals(newSource.get("palette_key").getAsString())) return this;
            } else if (obj.toString().equals(newSource.toString())) {
                return this;
            }
        }

        sources.add(newSource);
        return this;
    }

    @Override
    public void emit(Map<String, byte[]> files) {
        JsonObject root = new JsonObject();
        JsonArray srcArray = new JsonArray();
        for (JsonElement src : sources) {
            srcArray.add(src);
        }
        root.add("sources", srcArray);
        files.put("assets/" + namespace + "/atlases/" + id + ".json", GSON.toJson(root).getBytes(StandardCharsets.UTF_8));
    }
}