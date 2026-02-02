package com.github.darksoulq.abyssallib.server.resource.asset;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.plugin.Plugin;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Model implements Asset {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final String namespace;
    private final String path;

    private final byte[] rawData;

    private int[] textureSize = null;
    private GuiLight guiLight = GuiLight.FRONT;
    private boolean ambientOcclusion = true;
    private String parent;

    private final List<Element> elements = new ArrayList<>();
    private final Map<String, Texture> textures = new HashMap<>();
    private final Map<String, Display> display = new HashMap<>();

    public Model(Plugin plugin, String namespace, String path) {
        this.namespace = namespace;
        this.path = path;
        String resourcePath = "resourcepack/" + namespace + "/models/" + path + ".json";
        try (InputStream in = plugin.getResource(resourcePath)) {
            if (in == null) throw new RuntimeException("Model not found: " + resourcePath);
            this.rawData = in.readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load model: " + resourcePath, e);
        }
    }

    public Model(String namespace, String path, byte[] data) {
        this.namespace = namespace;
        this.path = path;
        this.rawData = data;
    }

    public Model(String namespace, String path) {
        this(namespace, path, 16, 16);
    }

    public Model(String namespace, String path, int texWidth, int texHeight) {
        this.namespace = namespace;
        this.path = path;
        this.rawData = null;
        this.textureSize = new int[]{texWidth, texHeight};
    }

    public Model parent(String model) {
        this.parent = model;
        return this;
    }

    public Model guiLight(GuiLight mode) {
        this.guiLight = mode;
        return this;
    }

    public Model ambientOcclusion(boolean ambientOcclusion) {
        this.ambientOcclusion = ambientOcclusion;
        return this;
    }

    public Model texture(String key, Texture value) {
        this.textures.put(key, value);
        return this;
    }

    public Element addElement(float[] from, float[] to) {
        Element element = new Element(from, to);
        this.elements.add(element);
        return element;
    }

    public Model display(String perspective, Display d) {
        this.display.put(perspective, d);
        return this;
    }

    public String file() {
        return namespace + ':' + path;
    }

    @Override
    public void emit(Map<String, byte[]> files) {
        String filePath = "assets/" + namespace + "/models/" + path + ".json";

        if (rawData != null) {
            files.put(filePath, rawData);
            return;
        }

        JsonObject obj = new JsonObject();

        if (parent != null) {
            obj.addProperty("parent", parent);
        }

        if (textureSize != null && (textureSize[0] != 16 || textureSize[1] != 16)) {
            obj.add("texture_size", toArray(textureSize));
        }

        obj.addProperty("gui_light", guiLight.name().toLowerCase());

        if (!ambientOcclusion) {
            obj.addProperty("ambientocclusion", false);
        }

        if (!textures.isEmpty()) {
            JsonObject tex = new JsonObject();
            for (Map.Entry<String, Texture> e : textures.entrySet()) {
                tex.addProperty(e.getKey(), e.getValue().file());
            }
            obj.add("textures", tex);
        }

        if (!elements.isEmpty()) {
            JsonArray array = new JsonArray();
            for (Element e : elements) {
                array.add(e.toJson());
            }
            obj.add("elements", array);
        }

        if (!display.isEmpty()) {
            JsonObject displayObj = new JsonObject();
            for (Map.Entry<String, Display> e : display.entrySet()) {
                displayObj.add(e.getKey(), e.getValue().toJson());
            }
            obj.add("display", displayObj);
        }

        files.put(filePath, GSON.toJson(obj).getBytes(StandardCharsets.UTF_8));
    }

    private JsonArray toArray(int[] arr) {
        JsonArray a = new JsonArray();
        for (int f : arr) a.add(f);
        return a;
    }

    public static class Element {
        private final float[] from;
        private final float[] to;
        private Rotation rotation;
        private int lightEmission = 0;
        private boolean shade = true;
        private final Map<String, Face> faces = new HashMap<>();

        public Element(float[] from, float[] to) {
            this.from = from;
            this.to = to;
        }

        public Element rotation(float[] origin, String axis, float angle, boolean rescale) {
            this.rotation = new Rotation(origin, axis, angle, rescale);
            return this;
        }

        public Element face(String dir, Face face) {
            this.faces.put(dir, face);
            return this;
        }

        public Element lightEmission(int lightEmission) {
            this.lightEmission = Math.max(0, Math.min(15, lightEmission));
            return this;
        }

        public Element shade(boolean shade) {
            this.shade = shade;
            return this;
        }

        public JsonObject toJson() {
            JsonObject o = new JsonObject();
            o.add("from", toArray(from));
            o.add("to", toArray(to));
            if (rotation != null) o.add("rotation", rotation.toJson());
            if (lightEmission != 0) o.addProperty("light_emission", lightEmission);
            if (!shade) o.addProperty("shade", false);

            JsonObject faceObj = new JsonObject();
            for (Map.Entry<String, Face> entry : faces.entrySet()) {
                faceObj.add(entry.getKey(), entry.getValue().toJson());
            }
            o.add("faces", faceObj);
            return o;
        }

        private JsonArray toArray(float[] arr) {
            JsonArray a = new JsonArray();
            for (float f : arr) a.add(f);
            return a;
        }

        private static class Rotation {
            float[] origin;
            String axis;
            float angle;
            boolean rescale;

            Rotation(float[] origin, String axis, float angle, boolean rescale) {
                this.origin = origin;
                this.axis = axis;
                this.angle = angle;
                this.rescale = rescale;
            }

            JsonObject toJson() {
                JsonObject o = new JsonObject();
                JsonArray a = new JsonArray();
                for (float f : origin) a.add(f);
                o.add("origin", a);
                o.addProperty("axis", axis);
                o.addProperty("angle", angle);
                if (rescale) o.addProperty("rescale", true);
                return o;
            }
        }
    }

    public static class Face {
        private final String texture;
        private float[] uv;
        private String cullface;
        private Integer rotation;
        private Integer tintindex;

        public Face(String texture) {
            this.texture = texture;
        }

        public Face uv(float u1, float v1, float u2, float v2) {
            this.uv = new float[]{u1, v1, u2, v2};
            return this;
        }

        public Face cullface(String face) {
            this.cullface = face;
            return this;
        }

        public Face rotation(int angle) {
            this.rotation = angle;
            return this;
        }

        public Face tint(int index) {
            this.tintindex = index;
            return this;
        }

        public JsonObject toJson() {
            JsonObject o = new JsonObject();
            o.addProperty("texture", texture);
            if (uv != null) {
                JsonArray a = new JsonArray();
                for (float f : uv) a.add(f);
                o.add("uv", a);
            }
            if (cullface != null) o.addProperty("cullface", cullface);
            if (rotation != null) o.addProperty("rotation", rotation);
            if (tintindex != null) o.addProperty("tintindex", tintindex);
            return o;
        }
    }

    public static class Display {
        private float[] rotation = new float[]{0, 0, 0};
        private float[] translation = new float[]{0, 0, 0};
        private float[] scale = new float[]{1, 1, 1};

        public Display rotation(float x, float y, float z) {
            this.rotation = new float[]{x, y, z};
            return this;
        }

        public Display translation(float x, float y, float z) {
            this.translation = new float[]{x, y, z};
            return this;
        }

        public Display scale(float x, float y, float z) {
            this.scale = new float[]{x, y, z};
            return this;
        }

        public JsonObject toJson() {
            JsonObject obj = new JsonObject();
            obj.add("rotation", toArray(rotation));
            obj.add("translation", toArray(translation));
            obj.add("scale", toArray(scale));
            return obj;
        }

        private JsonArray toArray(float[] vals) {
            JsonArray a = new JsonArray();
            for (float v : vals) a.add(v);
            return a;
        }
    }

    public enum GuiLight {
        FRONT, SIDE
    }
}