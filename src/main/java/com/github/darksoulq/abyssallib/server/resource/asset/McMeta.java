package com.github.darksoulq.abyssallib.server.resource.asset;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.github.darksoulq.abyssallib.util.FileUtils.GSON;

public class McMeta implements Asset {
    private final String path;
    private final String namespace;
    private int defaultFrameTime;
    private Boolean interpolate;
    private int width = -99;
    private int height = -99;
    private final List<Object> frames = new ArrayList<>();

    public McMeta(Plugin plugin, String namespace, @NotNull String path, boolean autoLoad) {
        this.path = path;
        this.namespace = namespace;

        if (!autoLoad) return;

        String resourcePath = "resourcepack/" + namespace + "/textures/" + path + ".png.mcmeta";
        try (InputStream in = plugin.getResource(resourcePath)) {
            if (in == null)
                throw new IllegalStateException("McMeta not found in plugin JAR at: " + resourcePath);

            JsonObject root = GSON.fromJson(new java.io.InputStreamReader(in), JsonObject.class);
            JsonObject anim = root.getAsJsonObject("animation");
            if (anim == null)
                throw new IllegalStateException("Missing 'animation' object in: " + resourcePath);

            if (anim.has("frametime")) this.defaultFrameTime = anim.get("frametime").getAsInt();
            if (anim.has("interpolate")) this.interpolate = anim.get("interpolate").getAsBoolean();
            if (anim.has("width")) this.width = anim.get("width").getAsInt();
            if (anim.has("height")) this.height = anim.get("height").getAsInt();

            if (anim.has("frames")) {
                JsonArray arr = anim.getAsJsonArray("frames");
                for (var el : arr) {
                    if (el.isJsonPrimitive()) {
                        frames.add(el.getAsInt());
                    } else {
                        Map<String, Object> frameObj = GSON.fromJson(el, Map.class);
                        frames.add(frameObj);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load mcmeta from: " + resourcePath, e);
        }
    }

    public McMeta frametime(int t) { this.defaultFrameTime = t; return this; }
    public McMeta interpolate(boolean b) { this.interpolate = b; return this; }
    public McMeta width(int w) {
        this.width = w;
        return this;
    }
    public McMeta height(int h) {
        this.height = h;
        return this;
    }
    public McMeta frames(int... idxs) {
        for (int i : idxs) frames.add(i); return this;
    }
    @SafeVarargs
    public final McMeta framesWithTime(Map<String, Object>... data) {
        Collections.addAll(frames, data);
        return this;
    }

    @Override
    public void emit(@NotNull Map<String, byte[]> files) {
        JsonObject root = new JsonObject();
        JsonObject anim = new JsonObject();
        anim.addProperty("frametime", defaultFrameTime);
        if (width != -99) anim.addProperty("width", width);
        if (height != -99) anim.addProperty("height", height);
        if (interpolate != null) anim.addProperty("interpolate", interpolate);

        JsonArray arr = new JsonArray();
        for (Object f : frames) {
            if (f instanceof Integer) arr.add((Integer) f);
            else if (f instanceof Map<?,?>) arr.add(GSON.toJsonTree(f));
        }
        if (!frames.isEmpty()) anim.add("frames", arr);

        root.add("animation", anim);
        files.put("assets/" + namespace + "/textures/" + path + ".png.mcmeta", GSON.toJson(root).getBytes(StandardCharsets.UTF_8));
    }
}
