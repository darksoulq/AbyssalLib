package com.github.darksoulq.abyssallib.server.resource.asset;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.github.darksoulq.abyssallib.common.util.TextUtil.GSON;

/**
 * Represents a blockstate JSON: {@code assets/<namespace>/blockstates/<id>.json}
 */
public class BlockState implements Asset {
    private final @NotNull String namespace;
    private final @NotNull String id;
    private byte[] rawData;

    private final Map<String, List<Variant>> variants = new LinkedHashMap<>();
    private final List<Multipart> multiparts = new ArrayList<>();

    public BlockState(@NotNull String namespace, @NotNull String id) {
        this.namespace = namespace;
        this.id = id;
    }

    public BlockState(@NotNull Plugin plugin, @NotNull String namespace, @NotNull String id) {
        this.namespace = namespace;
        this.id = id;
        String resource = "resourcepack/" + namespace + "/blockstates/" + id + ".json";
        try (InputStream in = plugin.getResource(resource)) {
            if (in == null) throw new IllegalStateException("Blockstate file not found: " + resource);
            this.rawData = in.readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load blockstate JSON", e);
        }
    }

    /** Adds a variant mapping for a variant key (e.g. "", "axis=x") */
    public BlockState variant(@NotNull String variantKey, Variant variant) {
        variants.computeIfAbsent(variantKey, k -> new ArrayList<>()).add(variant);
        return this;
    }

    /** Adds a multipart condition+model object */
    public BlockState multipart(@NotNull Multipart multipart) {
        this.multiparts.add(multipart);
        return this;
    }

    @Override
    public void emit(@NotNull Map<String, byte[]> files) {
        if (rawData != null) {
            files.put(path(), rawData);
            return;
        }

        JsonObject root = new JsonObject();
        if (!variants.isEmpty()) {
            JsonObject varObj = new JsonObject();
            for (var e : variants.entrySet()) {
                JsonArray arr = new JsonArray();
                for (Variant v : e.getValue()) arr.add(v.toJson());
                varObj.add(e.getKey(), arr);
            }
            root.add("variants", varObj);
        }
        if (!multiparts.isEmpty()) {
            JsonArray mo = new JsonArray();
            for (Multipart m : multiparts) mo.add(m.toJson());
            root.add("multipart", mo);
        }

        files.put(path(), GSON.toJson(root).getBytes(StandardCharsets.UTF_8));
    }

    private String path() {
        return "assets/" + namespace + "/blockstates/" + id + ".json";
    }

    public static class Variant {
        private final Model model;
        private Integer x, y, weight;
        private Boolean uvlock;

        public Variant(@NotNull Model model) { this.model = model; }

        public Variant x(int x) { this.x = x; return this; }
        public Variant y(int y) { this.y = y; return this; }
        public Variant weight(int w) { this.weight = w; return this; }
        public Variant uvlock(boolean b) { this.uvlock = b; return this; }

        public JsonObject toJson() {
            JsonObject o = new JsonObject();
            o.addProperty("model", model.file());
            if (x != null) o.addProperty("x", x);
            if (y != null) o.addProperty("y", y);
            if (weight != null) o.addProperty("weight", weight);
            if (uvlock != null) o.addProperty("uvlock", uvlock);
            return o;
        }
    }

    public static class Multipart {
        private final String whenCondition;
        private final Variant thenVariant;

        public Multipart(@NotNull String whenCondition, @NotNull Variant thenVariant) {
            this.whenCondition = whenCondition;
            this.thenVariant = thenVariant;
        }

        public JsonObject toJson() {
            JsonObject m = new JsonObject();
            if (!whenCondition.isEmpty()) {
                JsonObject cond = new JsonObject();
                cond.addProperty("when", whenCondition);
                m.add("when", cond);
            }
            m.add("apply", thenVariant.toJson());
            return m;
        }
    }
}
