package com.github.darksoulq.abyssallib.server.resource.asset;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.github.darksoulq.abyssallib.common.util.FileUtils.GSON;

public class WaypointStyle implements Asset {
    private final String namespace;
    private final String id;
    private final byte[] json;

    public WaypointStyle(@NotNull Plugin plugin, @NotNull String namespace, @NotNull String id) {
        this.namespace = namespace;
        this.id = id;
        try (InputStream in = plugin.getResource("resourcepack/" + namespace + "/waypoint_style/" + id + ".json")) {
            if (in == null) throw new RuntimeException("WaypointStyle not found: " + id);
            this.json = in.readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load WaypointStyle: " + id, e);
        }
    }

    private float nearDistance, farDistance;
    private final List<String> sprites = new ArrayList<>();

    public WaypointStyle(String namespace, String id) {
        this.namespace = namespace;
        this.id = id;
        this.json = null;
    }

    public WaypointStyle near(float d) {
        this.nearDistance = d;
        return this;
    }

    public WaypointStyle far(float d) {
        this.farDistance = d;
        return this;
    }

    public WaypointStyle sprite(Texture texture) {
        this.sprites.add(texture.file());
        return this;
    }

    @Override
    public void emit(Map<String, byte[]> files) {
        if (json != null) {
            files.put("assets/" + namespace + "/waypoint_style/" + id + ".json", json);
            return;
        }

        JsonObject root = new JsonObject();
        root.addProperty("near_distance", nearDistance);
        root.addProperty("far_distance", farDistance);
        JsonArray arr = new JsonArray();
        for (String s : sprites) arr.add(s);
        root.add("sprites", arr);

        files.put("assets/" + namespace + "/waypoint_style/" + id + ".json",
                GSON.toJson(root).getBytes(StandardCharsets.UTF_8));
    }
}
