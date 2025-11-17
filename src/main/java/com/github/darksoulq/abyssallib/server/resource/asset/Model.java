package com.github.darksoulq.abyssallib.server.resource.asset;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.plugin.Plugin;
import org.checkerframework.common.value.qual.IntRange;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a model used in the resource pack. Can be loaded from a plugin JAR or defined programmatically.
 */
@ApiStatus.Experimental
public class Model implements Asset {

    /**
     * The namespace for this model.
     */
    private final String namespace;

    /**
     * The relative path to the model inside the namespace.
     */
    private final String path;

    /**
     * Raw JSON model data if loaded from the JAR. Null if defined programmatically.
     */
    private final byte[] rawData;

    /**
     * the size of the textures.
     */
    private int[] textureSize = null;

    /**
     * the direction from where light should fall on item in the GUI.
     */
    private GuiLight guiLight = GuiLight.FRONT;

    /**
     * whether ambient occlusion should be enabled.
     */
    private boolean ambientOcclusion = true;

    /**
     * List of geometric elements defined for the model.
     */
    private final List<Element> elements = new ArrayList<>();

    /**
     * Texture references used in this model.
     */
    private final Map<String, Texture> textures = new HashMap<>();

    /**
     * Display transformation definitions for various perspectives.
     */
    private final Map<String, Display> display = new HashMap<>();

    /**
     * Optional parent model identifier.
     */
    private String parent;

    /**
     * Loads a model from the plugin JAR located at {@code resourcepack/<namespace>/models/<path>.json}.
     *
     * @param plugin    The plugin instance
     * @param namespace The namespace of the model
     * @param path      The path of the model inside the namespace
     */
    public Model(@NotNull Plugin plugin, @NotNull String namespace, @NotNull String path) {
        this.namespace = namespace;
        this.path = path;
        String resourcePath = "resourcepack/" + namespace + "/models/" + path + ".json";
        try (InputStream in = plugin.getResource(resourcePath)) {
            if (in == null) throw new IllegalStateException("Model not found in plugin JAR at: " + resourcePath);
            this.rawData = in.readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load model from plugin JAR: " + resourcePath, e);
        }
    }

    /**
     * Creates a new, empty model to be defined programmatically.
     *
     * @param namespace The model namespace
     * @param path      The model path within the namespace
     */
    public Model(@NotNull String namespace, @NotNull String path) {
        this(namespace, path, 16, 16);
    }
    /**
     * Creates a new, empty model to be defined programmatically.
     *
     * @param namespace The model namespace
     * @param path      The model path within the namespace
     * @param texWidth The width of the textures.
     * @param texHeight The height of the textures
     */
    public Model(@NotNull String namespace, @NotNull String path, int texWidth, int texHeight) {
        this.namespace = namespace;
        this.path = path;
        this.rawData = null;
        this.textureSize = new int[] {texWidth, texHeight};
    }

    public Model(String namespace, String path, byte[] data) {
        this.namespace = namespace;
        this.path = path;
        this.rawData = data;
    }

    /**
     * Sets the parent model.
     *
     * @param model The namespaced parent identifier
     * @return This model for chaining
     */
    public @NotNull Model parent(@NotNull String model) {
        this.parent = model;
        return this;
    }

    /**
     * Sets the direction from where light should fall on item in the GUI.
     *
     * @param mode The direction of the light
     * @return This model for chaining
     */
    public @NotNull Model guiLight(GuiLight mode) {
        this.guiLight = mode;
        return this;
    }

    /**
     * Sets whether ambient occlusion should be enabled.
     * @param ambientOcclusion whether ambient occlusion should be enabled.
     * @return This model for chaining.
     */
    public @NotNull Model ambientOcculsion(boolean ambientOcclusion) {
        this.ambientOcclusion = ambientOcclusion;
        return this;
    }

    /**
     * Adds a texture reference.
     *
     * @param key   Texture key (e.g., "layer0")
     * @param value The texture asset
     * @return This model for chaining
     */
    public @NotNull Model texture(@NotNull String key, @NotNull Texture value) {
        this.textures.put(key, value);
        return this;
    }

    /**
     * Adds a cuboid element to the model.
     *
     * @param from Minimum XYZ coordinate
     * @param to   Maximum XYZ coordinate
     * @return The created element
     */
    public @NotNull Element addElement(@NotNull float[] from, @NotNull float[] to) {
        Element element = new Element(from, to);
        this.elements.add(element);
        return element;
    }

    /**
     * Adds display settings for a specific perspective.
     *
     * @param perspective The display perspective (e.g., "thirdperson_righthand")
     * @param d           The display transformation definition
     * @return This model for chaining
     */
    public @NotNull Model display(@NotNull String perspective, @NotNull Display d) {
        this.display.put(perspective, d);
        return this;
    }

    /**
     * Returns the namespaced model file ID.
     *
     * @return File ID in {@code namespace:path} format
     */
    public @NotNull String file() {
        return namespace + ':' + path;
    }

    @Override
    public void emit(@NotNull Map<String, byte[]> files) {
        if (rawData != null) {
            files.put("assets/" + namespace + "/models/" + path + ".json", rawData);
            return;
        }

        JsonObject obj = new JsonObject();
        if (textureSize[0] != 16 && textureSize[1] != 16) obj.add("texture_size", toArray(textureSize));
        if (parent != null) obj.addProperty("parent", parent);
        obj.addProperty("gui_light", guiLight.name().toLowerCase());
        if (!ambientOcclusion) obj.addProperty("ambientocclusion", false);

        if (!textures.isEmpty()) {
            JsonObject tex = new JsonObject();
            for (Map.Entry<String, Texture> e : textures.entrySet())
                tex.addProperty(e.getKey(), e.getValue().file());
            obj.add("textures", tex);
        }

        if (!elements.isEmpty()) {
            JsonArray array = new JsonArray();
            for (Element e : elements) array.add(e.toJson());
            obj.add("elements", array);
        }

        if (!display.isEmpty()) {
            JsonObject displayObj = new JsonObject();
            for (Map.Entry<String, Display> e : display.entrySet())
                displayObj.add(e.getKey(), e.getValue().toJson());
            obj.add("display", displayObj);
        }

        files.put("assets/" + namespace + "/models/" + path + ".json",
                new GsonBuilder().setPrettyPrinting().create().toJson(obj).getBytes(StandardCharsets.UTF_8));
    }

    private JsonArray toArray(int[] arr) {
        JsonArray a = new JsonArray();
        for (int f : arr) a.add(f);
        return a;
    }

    /**
     * Represents a cube element in a block/item model.
     */
    @ApiStatus.Experimental
    public static class Element {
        /**
         * How much light this element should emit.
         */
        private int lightEmission = 0;
        /**
         * Whether to apply directional shading.
         */
        private boolean shade = true;

        /**
         * Minimum corner coordinates.
         */
        private final float[] from;

        /**
         * Maximum corner coordinates.
         */
        private final float[] to;

        /**
         * Optional rotation applied to this element.
         */
        private Rotation rotation;

        /**
         * Face definitions for each direction.
         */
        private final Map<String, Face> faces = new HashMap<>();

        public Element(float @NotNull [] from, float @NotNull [] to) {
            this.from = from;
            this.to = to;
        }

        public @NotNull Element rotation(float[] origin, String axis, float angle, boolean rescale) {
            this.rotation = new Rotation(origin, axis, angle, rescale);
            return this;
        }

        public @NotNull Element face(String dir, Face face) {
            this.faces.put(dir, face);
            return this;
        }

        public @NotNull Element lightEmission(@IntRange(from = 0, to = 15) int lightEmission) {
            this.lightEmission = lightEmission;
            return this;
        }

        public @NotNull Element shade(boolean shade) {
            this.shade = shade;
            return this;
        }

        public @NotNull JsonObject toJson() {
            JsonObject o = new JsonObject();
            o.add("from", toArray(from));
            o.add("to", toArray(to));
            if (rotation != null) o.add("rotation", rotation.toJson());
            if (lightEmission != 0) o.addProperty("light_emission", lightEmission);
            if (!shade) o.addProperty("shade", false);
            if (lightEmission != 0) o.addProperty("light_emission", lightEmission);

            JsonObject faceObj = new JsonObject();
            for (var entry : faces.entrySet()) {
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

        /**
         * Represents rotation settings for an element.
         */
        @ApiStatus.Experimental
        private static class Rotation {

            /**
             * The origin point of rotation.
             */
            float[] origin;

            /**
             * Axis of rotation: "x", "y", or "z".
             */
            String axis;

            /**
             * Rotation angle in degrees.
             */
            float angle;

            /**
             * Whether to apply rescaling.
             */
            boolean rescale;

            Rotation(float[] origin, String axis, float angle, boolean rescale) {
                this.origin = origin;
                this.axis = axis;
                this.angle = angle;
                this.rescale = rescale;
            }

            JsonObject toJson() {
                JsonObject o = new JsonObject();
                o.add("origin", toArray(origin));
                o.addProperty("axis", axis);
                o.addProperty("angle", angle);
                o.addProperty("rescale", rescale);
                return o;
            }

            private JsonArray toArray(float[] arr) {
                JsonArray a = new JsonArray();
                for (float f : arr) a.add(f);
                return a;
            }
        }
    }

    /**
     * Represents a face of a model element.
     */
    @ApiStatus.Experimental
    public static class Face {

        /**
         * Texture reference (e.g. "#layer0").
         */
        private final String texture;

        /**
         * UV mapping coordinates.
         */
        private float[] uv;

        /**
         * Face used for culling.
         */
        private String cullface;

        /**
         * Rotation of the face texture.
         */
        private Integer rotation;

        /**
         * Tint index (used for block color overlays).
         */
        private Integer tintindex;

        public Face(@NotNull String texture) {
            this.texture = texture;
        }

        public @NotNull Face uv(float u1, float v1, float u2, float v2) {
            this.uv = new float[]{u1, v1, u2, v2};
            return this;
        }

        public @NotNull Face cullface(@NotNull String face) {
            this.cullface = face;
            return this;
        }

        public @NotNull Face rotation(int angle) {
            this.rotation = angle;
            return this;
        }

        public @NotNull Face tint(int index) {
            this.tintindex = index;
            return this;
        }

        public @NotNull JsonObject toJson() {
            JsonObject o = new JsonObject();
            o.addProperty("texture", texture);
            if (uv != null) o.add("uv", toArray(uv));
            if (cullface != null) o.addProperty("cullface", cullface);
            if (rotation != null) o.addProperty("rotation", rotation);
            if (tintindex != null) o.addProperty("tintindex", tintindex);
            return o;
        }

        private JsonArray toArray(float[] arr) {
            JsonArray a = new JsonArray();
            for (float f : arr) a.add(f);
            return a;
        }
    }

    /**
     * Represents display settings for a specific item or block perspective.
     */
    @ApiStatus.Experimental
    public static class Display {
        /**
         * Rotation angles in degrees for X, Y, Z axes.
         */
        private float[] rotation = new float[3];

        /**
         * Translation offset values along X, Y, Z axes.
         */
        private float[] translation = new float[3];

        /**
         * Scale factors for X, Y, Z axes. Defaults to {1, 1, 1}.
         */
        private float[] scale = new float[]{1, 1, 1};

        /**
         * Sets the display rotation for this perspective.
         *
         * @param x rotation around X axis in degrees
         * @param y rotation around Y axis in degrees
         * @param z rotation around Z axis in degrees
         * @return this display instance
         */
        public @NotNull Display rotation(float x, float y, float z) {
            this.rotation = new float[]{x, y, z};
            return this;
        }

        /**
         * Sets the display translation for this perspective.
         *
         * @param x translation on X axis
         * @param y translation on Y axis
         * @param z translation on Z axis
         * @return this display instance
         */
        public @NotNull Display translation(float x, float y, float z) {
            this.translation = new float[]{x, y, z};
            return this;
        }

        /**
         * Sets the display scale for this perspective.
         *
         * @param x scale factor on X axis
         * @param y scale factor on Y axis
         * @param z scale factor on Z axis
         * @return this display instance
         */
        public @NotNull Display scale(float x, float y, float z) {
            this.scale = new float[]{x, y, z};
            return this;
        }

        /**
         * Converts this display settings into a JSON object suitable for model output.
         *
         * @return JSON representation of display properties
         */
        public @NotNull JsonObject toJson() {
            JsonObject obj = new JsonObject();
            obj.add("rotation", toArray(rotation));
            obj.add("translation", toArray(translation));
            obj.add("scale", toArray(scale));
            return obj;
        }

        /**
         * Converts a float array to a JSON array.
         *
         * @param vals float array
         * @return corresponding JSON array
         */
        private @NotNull JsonArray toArray(float[] vals) {
            JsonArray a = new JsonArray();
            for (float v : vals) a.add(v);
            return a;
        }
    }

    public enum GuiLight {
        FRONT, SIDE
    }
}
