package com.github.darksoulq.abyssallib.server.resource.asset;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.plugin.Plugin;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class PostEffect implements Asset {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final String namespace;
    private final String id;

    private final byte[] rawData;

    private final List<String> targets = new LinkedList<>();
    private final List<Pass> passes = new LinkedList<>();

    public PostEffect(Plugin plugin, String namespace, String id) {
        this.namespace = namespace;
        this.id = id;
        String resource = "resourcepack/" + namespace + "/post_effect/" + id + ".json";
        try (InputStream in = plugin.getResource(resource)) {
            if (in == null) throw new RuntimeException("PostEffect not found: " + resource);
            this.rawData = in.readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load PostEffect: " + resource, e);
        }
    }

    public PostEffect(String namespace, String id, byte[] data) {
        this.namespace = namespace;
        this.id = id;
        this.rawData = data;
    }

    public PostEffect(String namespace, String id) {
        this.namespace = namespace;
        this.id = id;
        this.rawData = null;
    }

    public PostEffect addTarget(String target) {
        this.targets.add(target);
        return this;
    }

    public PostEffect addPass(String name, String inTarget, String outTarget, List<Uniform> uniforms) {
        passes.add(new Pass(name, inTarget, outTarget, uniforms));
        return this;
    }

    @Override
    public void emit(Map<String, byte[]> files) {
        String path = "assets/" + namespace + "/post_effect/" + id + ".json";

        if (rawData != null) {
            files.put(path, rawData);
            return;
        }

        JsonObject root = new JsonObject();

        if (!targets.isEmpty()) {
            JsonArray tgt = new JsonArray();
            targets.forEach(tgt::add);
            root.add("targets", tgt);
        }

        if (!passes.isEmpty()) {
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
                passArr.add(o);
            }
            root.add("passes", passArr);
        }

        files.put(path, GSON.toJson(root).getBytes(StandardCharsets.UTF_8));
    }

    public static class Uniform {
        public final String name;
        public final List<Double> values;
        public final String type;
        public final Integer count;

        public Uniform(String name, double[] values) {
            this(name, values, null, null);
        }

        public Uniform(String name, double[] values, String type, Integer count) {
            this.name = name;
            this.values = new ArrayList<>();
            for (double d : values) this.values.add(d);
            this.type = type;
            this.count = count;
        }
    }

    public static class Pass {
        final String name;
        final String inTarget;
        final String outTarget;
        final List<Uniform> uniforms;
        final List<String> samplers = new ArrayList<>();

        public Pass(String name, String in, String out, List<Uniform> u) {
            this.name = name;
            this.inTarget = in;
            this.outTarget = out;
            this.uniforms = u != null ? u : Collections.emptyList();
        }

        public Pass samplers(String... s) {
            Collections.addAll(this.samplers, s);
            return this;
        }
    }
}