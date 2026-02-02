package com.github.darksoulq.abyssallib.server.resource.asset;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.plugin.Plugin;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class McMeta implements Asset {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final String namespace;
    private final String path;

    private final byte[] rawData;

    private int frametime = 1;
    private Boolean interpolate;
    private Integer width;
    private Integer height;
    private final List<Object> frames = new ArrayList<>();

    public McMeta(Plugin plugin, String namespace, String path) {
        this.namespace = namespace;
        this.path = path;
        String file = "resourcepack/" + namespace + "/textures/" + path + ".png.mcmeta";
        try (InputStream in = plugin.getResource(file)) {
            if (in == null) throw new RuntimeException("McMeta not found: " + file);
            this.rawData = in.readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load mcmeta: " + file, e);
        }
    }

    public McMeta(String namespace, String path, byte[] data) {
        this.namespace = namespace;
        this.path = path;
        this.rawData = data;
    }

    public McMeta(String namespace, String path) {
        this.namespace = namespace;
        this.path = path;
        this.rawData = null;
    }

    public McMeta frametime(int frametime) {
        this.frametime = frametime;
        return this;
    }

    public McMeta interpolate(boolean interpolate) {
        this.interpolate = interpolate;
        return this;
    }

    public McMeta size(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public McMeta frame(int index) {
        this.frames.add(index);
        return this;
    }

    public McMeta frame(int index, int time) {
        JsonObject frame = new JsonObject();
        frame.addProperty("index", index);
        frame.addProperty("time", time);
        this.frames.add(frame);
        return this;
    }

    @Override
    public void emit(Map<String, byte[]> files) {
        String filePath = "assets/" + namespace + "/textures/" + path + ".png.mcmeta";

        if (rawData != null) {
            files.put(filePath, rawData);
            return;
        }

        JsonObject root = new JsonObject();
        JsonObject animation = new JsonObject();

        animation.addProperty("frametime", frametime);

        if (interpolate != null) {
            animation.addProperty("interpolate", interpolate);
        }

        if (width != null) animation.addProperty("width", width);
        if (height != null) animation.addProperty("height", height);

        if (!frames.isEmpty()) {
            JsonArray framesArray = new JsonArray();
            for (Object f : frames) {
                if (f instanceof Integer i) framesArray.add(i);
                else if (f instanceof JsonObject j) framesArray.add(j);
            }
            animation.add("frames", framesArray);
        }

        root.add("animation", animation);
        files.put(filePath, GSON.toJson(root).getBytes(StandardCharsets.UTF_8));
    }
}