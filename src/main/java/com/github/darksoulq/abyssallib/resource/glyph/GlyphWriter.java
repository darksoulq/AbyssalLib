package com.github.darksoulq.abyssallib.resource.glyph;

import com.google.gson.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

public class GlyphWriter {
    public static void write(JavaPlugin plugin, Glyph glyph) {
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
