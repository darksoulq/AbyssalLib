package com.github.darksoulq.abyssallib.server.resource.asset;

import com.google.gson.*;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Represents a custom font resource for Minecraft.
 * Supports auto-generation and auto-loading of glyphs using bitmap textures.
 */
@ApiStatus.Experimental
public class Font implements Asset {

    /**
     * The namespace this font belongs to.
     */
    private final String namespace;

    /**
     * The font identifier (filename without extension).
     */
    private final String id;

    /**
     * List of glyphs contained in this font.
     */
    private final List<@NotNull Glyph> glyphs = new ArrayList<>();

    /**
     * Set of Unicode characters already used in this font.
     */
    private final Set<Character> occupied = new HashSet<>();

    /**
     * Current base Unicode to assign from Private Use Area (PUA).
     */
    private int unicodeBase = 0xE000;

    /**
     * Constructs an empty font with a given namespace and ID.
     *
     * @param namespace the resource pack namespace
     * @param id        the font file ID (without extension)
     */
    public Font(@NotNull String namespace, @NotNull String id) {
        this.namespace = namespace;
        this.id = id;
    }

    /**
     * Automatically loads the font from the plugin JAR at:
     * {@code resourcepack/{namespace}/font/{id}.json}
     *
     * @param plugin    the plugin to load from
     * @param namespace the resource pack namespace
     * @param id        the font file ID (without extension)
     */
    public Font(@NotNull Plugin plugin, @NotNull String namespace, @NotNull String id) {
        this.namespace = namespace;
        this.id = id;

        String path = "resourcepack/" + namespace + "/font/" + id + ".json";
        try (InputStream in = plugin.getResource(path)) {
            if (in == null) throw new IllegalStateException("Font file not found at: " + path);

            JsonObject json = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
            JsonArray providers = json.getAsJsonArray("providers");

            for (JsonElement el : providers) {
                JsonObject provider = el.getAsJsonObject();
                if (!"bitmap".equals(provider.get("type").getAsString())) continue;

                String file = provider.get("file").getAsString();
                int ascent = provider.has("ascent") ? provider.get("ascent").getAsInt() : 8;
                int height = provider.has("height") ? provider.get("height").getAsInt() : 8;
                Integer advance = provider.has("advance") ? provider.get("advance").getAsInt() : null;

                Texture texture = new Texture(plugin, namespace, file);

                JsonArray chars = provider.getAsJsonArray("chars");
                for (JsonElement ch : chars) {
                    char c = ch.getAsString().charAt(0);
                    glyphs.add(new Glyph(texture, c, height, ascent, advance));
                    occupied.add(c);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to load font from JAR", e);
        }
    }

    /**
     * Creates and adds a new glyph with auto-assigned Unicode character.
     *
     * @param texture the glyph texture
     * @param height  glyph height
     * @param ascent  glyph ascent
     * @return the created {@link Glyph}
     */
    public @NotNull Glyph glyph(@NotNull Texture texture, int height, int ascent) {
        return glyph(texture, height, ascent, null);
    }

    /**
     * Creates and adds a new glyph with optional advance and auto-assigned Unicode character.
     *
     * @param texture the glyph texture
     * @param height  glyph height
     * @param ascent  glyph ascent
     * @param advance optional advance value (can be {@code null})
     * @return the created {@link Glyph}
     */
    public @NotNull Glyph glyph(@NotNull Texture texture, int height, int ascent, Integer advance) {
        char assigned = getNextAvailableUnicode();
        Glyph g = new Glyph(texture, assigned, height, ascent, advance);
        glyphs.add(g);
        occupied.add(assigned);
        return g;
    }

    /**
     * Finds and returns the next unused character in the Private Use Area (U+E000–U+F8FF).
     *
     * @return the assigned character
     * @throws IllegalStateException if no free character is available
     */
    private char getNextAvailableUnicode() {
        while (unicodeBase <= 0xF8FF) {
            char c = (char) unicodeBase++;
            if (!occupied.contains(c)) return c;
        }
        throw new IllegalStateException("No free Unicode chars left in Private Use Area.");
    }

    /**
     * Emits the font JSON file to the output map.
     *
     * @param files the file output map
     */
    @Override
    public void emit(@NotNull Map<String, byte[]> files) {
        JsonObject root = new JsonObject();
        JsonArray providers = new JsonArray();

        for (Glyph glyph : glyphs) {
            JsonObject provider = new JsonObject();
            provider.addProperty("type", "bitmap");
            provider.addProperty("file", glyph.texture().file());
            provider.addProperty("height", glyph.height());
            provider.addProperty("ascent", glyph.ascent());

            if (glyph.advance() != null) {
                provider.addProperty("advance", glyph.advance());
            }

            JsonArray chars = new JsonArray();
            chars.add(String.valueOf(glyph.character()));
            provider.add("chars", chars);

            providers.add(provider);
        }

        root.add("providers", providers);

        files.put("assets/" + namespace + "/font/" + id + ".json",
                new GsonBuilder().setPrettyPrinting().create().toJson(root).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Represents a single font glyph entry in a bitmap font provider.
     *
     * @param texture   the bitmap texture used for the glyph
     * @param character the character code assigned
     * @param height    the height of the glyph
     * @param ascent    the ascent of the glyph
     * @param advance   optional advance value
     */
    public record Glyph(
            @NotNull Texture texture,
            char character,
            int height,
            int ascent,
            Integer advance
    ) {}
}
