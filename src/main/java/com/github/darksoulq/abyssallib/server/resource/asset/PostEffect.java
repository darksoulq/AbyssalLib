package com.github.darksoulq.abyssallib.server.resource.asset;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.github.darksoulq.abyssallib.common.util.FileUtils.GSON;

public class PostEffect implements Asset {

    private final @NotNull String namespace;
    private final @NotNull String id;
    private byte[] rawData;

    private final List<String> targets = new ArrayList<>();
    private final List<Pass> passes = new ArrayList<>();

    public PostEffect(@NotNull String namespace, @NotNull String id) {
        this.namespace = namespace;
        this.id = id;
    }

    public PostEffect(@NotNull Plugin plugin, @NotNull String namespace, @NotNull String id) {
        this.namespace = namespace;
        this.id = id;
        String resource = "resourcepack/" + namespace + "/post_effect/" + id + ".json";
        try (InputStream in = plugin.getResource(resource)) {
            if (in == null) throw new IllegalStateException("PostEffect JSON not found: " + resource);
            this.rawData = in.readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load PostEffect JSON", e);
        }
    }

    public PostEffect addTarget(@NotNull String target) {
        this.targets.add(target);
        return this;
    }

    public PostEffect addPass(@NotNull String name, @NotNull String inTarget, @NotNull String outTarget,
                              @NotNull List<Uniform> uniforms) {
        passes.add(new Pass(name, inTarget, outTarget, uniforms));
        return this;
    }

    @Override
    public void emit(@NotNull Map<String, byte[]> files) {
        if (rawData != null) {
            files.put(path(), rawData);
            return;
        }

        JsonObject root = new JsonObject();
        if (!targets.isEmpty()) {
            JsonArray tgt = new JsonArray();
            targets.forEach(t -> tgt.add(t));
            root.add("targets", tgt);
        }

        JsonArray passArr = new JsonArray();
        for (Pass p : passes) {
            JsonObject o = new JsonObject();
            o.addProperty("name", p.name);
            o.addProperty("intarget", p.inTarget);
            o.addProperty("outtarget", p.outTarget);
            if (!p.uniforms.isEmpty()) {
                JsonArray uarr = new JsonArray();
                for (Uniform u : p.uniforms) {
                    JsonObject uo = new JsonObject();
                    uo.addProperty("name", u.name);
                    JsonArray vals = new JsonArray();
                    for (Number v : u.values) vals.add(v.doubleValue());
                    uo.add("values", vals);
                    if (u.type != null) uo.addProperty("type", u.type);
                    if (u.count != null) uo.addProperty("count", u.count);
                    uarr.add(uo);
                }
                o.add("uniforms", uarr);
            }
            if (!p.samplers.isEmpty()) {
                JsonArray sarr = new JsonArray();
                p.samplers.forEach(sarr::add);
                o.add("samplers", sarr);
            }
            root.add("passes", passArr);
            passArr.add(o);
        }

        files.put(path(), GSON.toJson(root).getBytes(StandardCharsets.UTF_8));
    }

    private String path() {
        return "assets/" + namespace + "/post_effect/" + id + ".json";
    }

    public static class Uniform {
        public final String name;
        public final List<Double> values;
        public final String type;
        public final Integer count;

        public Uniform(@NotNull String name, @NotNull double[] values) {
            this.name = name;
            this.values = new ArrayList<>();
            for (double d : values) this.values.add(d);
            this.type = null;
            this.count = null;
        }

        public Uniform(@NotNull String name, @NotNull double[] values, @NotNull String type, Integer count) {
            this.name = name;
            this.values = new ArrayList<>();
            for (double d : values) this.values.add(d);
            this.type = type;
            this.count = count;
        }
    }

    private static class Pass {
        final String name, inTarget, outTarget;
        final List<Uniform> uniforms;
        final List<String> samplers = new ArrayList<>();

        Pass(String name, String in, String out, List<Uniform> u) {
            this.name = name;
            this.inTarget = in;
            this.outTarget = out;
            this.uniforms = u != null ? u : Collections.emptyList();
        }
        Pass samplers(String... s) {
            this.samplers.addAll(Arrays.asList(s));
            return this;
        }
    }
}
