package com.github.darksoulq.abyssallib.resource.glyph;

import com.google.gson.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlyphWriter {
    private static final Map<String, List<Glyph>> GLYPH_MAP = new HashMap<>();

    /**
     * Registers a glyph for later writing to a font definition file.
     * Glyphs are grouped internally by their namespace (mod ID).
     * This method is internally, do not call it yourself.
     *
     * @param glyph the glyph to register
     */
    public static void registerGlyph(Glyph glyph) {
        GLYPH_MAP
                .computeIfAbsent(glyph.id().namespace(), ns -> new ArrayList<>())
                .add(glyph);
    }

    /**
     * Writes all registered glyphs for the given mod ID to the appropriate {@code default.json}
     * resource pack font file.
     * <p>
     * Each glyph is added as a new provider in the Minecraft font system. This method ensures that
     * all glyphs under the same mod ID are written using their associated plugin instance.
     * </p>
     *
     * @param modid the namespace of the glyphs to write (e.g., "myplugin")
     */
    public static void write(String modid) {
        List<Glyph> glyphs = GLYPH_MAP.get(modid);
        for (Glyph glyph : glyphs) {
            write(glyph.plugin, glyph);
        }
    }

    /**
     * Writes a single glyph entry into the {@code default.json} font file for the specified plugin.
     * If the file already exists, it appends the glyph to the existing "providers" array.
     * Otherwise, it creates a new file with the required structure.
     *
     * @param plugin the plugin instance used to resolve the file path
     * @param glyph  the glyph to write
     */
    private static void write(JavaPlugin plugin, Glyph glyph) {
        File fontFile = new File(plugin.getDataFolder(), "pack/resourcepack/assets/minecraft/font/default.json");

        JsonObject root;
        JsonArray providers;

        if (fontFile.exists()) {
            try (Reader reader = new FileReader(fontFile)) {
                root = JsonParser.parseReader(reader).getAsJsonObject();
                providers = root.has("providers") ? root.getAsJsonArray("providers") : new JsonArray();
            } catch (IOException e) {
                throw new RuntimeException("Failed to read existing font definition", e);
            }
        } else {
            root = new JsonObject();
            providers = new JsonArray();
            root.add("providers", providers);
            fontFile.getParentFile().mkdirs();
        }

        JsonObject glyphJson = new JsonObject();
        glyphJson.addProperty("type", "bitmap");
        glyphJson.addProperty("file", glyph.id().namespace() + ":" + glyph.file() + ".png");
        glyphJson.addProperty("ascent", glyph.ascent());
        glyphJson.addProperty("height", glyph.height());

        JsonArray chars = new JsonArray();
        chars.add(String.valueOf(glyph.unicode()));
        glyphJson.add("chars", chars);

        providers.add(glyphJson);

        try (Writer writer = new FileWriter(fontFile)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(root, writer);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write glyph definition", e);
        }
    }
}