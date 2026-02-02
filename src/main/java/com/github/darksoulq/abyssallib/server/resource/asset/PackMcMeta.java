package com.github.darksoulq.abyssallib.server.resource.asset;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.plugin.Plugin;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class PackMcMeta implements Asset {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final byte[] rawData;

    private int packFormat = 34;
    private Object description = "";
    private Integer minFormat;
    private Integer maxFormat;

    public PackMcMeta(Plugin plugin) {
        String path = "resourcepack/pack.mcmeta";
        try (InputStream in = plugin.getResource(path)) {
            if (in == null) throw new RuntimeException("pack.mcmeta not found in plugin: " + path);
            this.rawData = in.readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load pack.mcmeta", e);
        }
    }

    public PackMcMeta(byte[] data) {
        this.rawData = data;
    }

    public PackMcMeta() {
        this.rawData = null;
    }

    public PackMcMeta packFormat(int format) {
        this.packFormat = format;
        return this;
    }

    public PackMcMeta description(String description) {
        this.description = description;
        return this;
    }

    public PackMcMeta description(Component description) {
        this.description = description;
        return this;
    }

    public PackMcMeta supportedFormats(int min, int max) {
        this.minFormat = min;
        this.maxFormat = max;
        return this;
    }

    @Override
    public void emit(Map<String, byte[]> files) {
        if (rawData != null) {
            files.put("pack.mcmeta", rawData);
            return;
        }

        JsonObject root = new JsonObject();
        JsonObject pack = new JsonObject();

        pack.addProperty("pack_format", packFormat);

        if (description instanceof Component c) {
            pack.add("description", GsonComponentSerializer.gson().serializeToTree(c));
        } else {
            pack.addProperty("description", String.valueOf(description));
        }

        if (minFormat != null && maxFormat != null) {
            JsonObject supported = new JsonObject();
            supported.addProperty("min_inclusive", minFormat);
            supported.addProperty("max_inclusive", maxFormat);
            pack.add("supported_formats", supported);
        }

        root.add("pack", pack);
        files.put("pack.mcmeta", GSON.toJson(root).getBytes(StandardCharsets.UTF_8));
    }
}