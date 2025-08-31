package com.github.darksoulq.abyssallib.server.resource.asset;

import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static com.github.darksoulq.abyssallib.common.util.FileUtils.GSON;

/**
 * Represents the {@code pack.mcmeta} metadata file.
 */
public class PackMcMeta implements Asset {

    private int packFormat;
    private Object description;
    private Integer minFormat, maxFormat;
    private byte[] rawData;

    /**
     * Loads {@code pack.mcmeta} directly from {@code resourcepack/pack.mcmeta} inside the plugin JAR.
     */
    public PackMcMeta(@NotNull Plugin plugin) {
        try (InputStream in = plugin.getResource("resourcepack/pack.mcmeta")) {
            if (in == null) throw new IllegalStateException("Missing pack.mcmeta in resourcepack folder.");
            this.rawData = in.readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load pack.mcmeta", e);
        }
    }

    /**
     * Creates a new programmatically defined {@code pack.mcmeta}.
     */
    public PackMcMeta() {
        this.rawData = null;
    }

    public PackMcMeta packFormat(int fmt) {
        this.packFormat = fmt;
        return this;
    }

    public PackMcMeta description(@NotNull String desc) {
        this.description = desc;
        return this;
    }

    public PackMcMeta description(@NotNull Component component) {
        this.description = component;
        return this;
    }

    public PackMcMeta supportedFormats(int minIncl, int maxIncl) {
        this.minFormat = minIncl;
        this.maxFormat = maxIncl;
        return this;
    }

    @Override
    public void emit(@NotNull Map<String, byte[]> files) {
        if (rawData != null) {
            files.put("pack.mcmeta", rawData);
            return;
        }

        JsonObject root = new JsonObject();
        JsonObject pack = new JsonObject();

        pack.addProperty("pack_format", packFormat);

        if (description instanceof String s) {
            pack.addProperty("description", s);
        } else if (description instanceof Component c) {
            var jsonElement = GsonComponentSerializer.gson().serializeToTree(c);
            pack.add("description", jsonElement);
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
