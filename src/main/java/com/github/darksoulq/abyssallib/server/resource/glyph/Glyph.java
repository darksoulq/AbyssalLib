package com.github.darksoulq.abyssallib.server.resource.glyph;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.world.level.data.Identifier;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: REWRITE GLYPHS SYSTEM TO ALLOW MULTI-FILES

/**
 * Represents a custom glyph in the system, including its placeholder, Unicode, and appearance properties.
 * Glyphs can be used for text replacement in chat and other places within the game.
 * Glyphs are registered and replaced using their placeholders.
 */
public class Glyph {
    private static final Map<String, Character> CHAT_MAP = new HashMap<>();
    private static final Map<String, Character> PLACEHOLDER_MAP = new HashMap<>();
    private static final List<Glyph> REGISTERED_GLYPHS = new ArrayList<>();
    private static char nextUnicode = '\ue000';

    public final JavaPlugin plugin;
    private final Identifier id;
    private final String placeholder;
    private final boolean inChat;
    private final String file;
    private final int height;
    private final int ascent;
    private char unicode;

    /**
     * Creates a new Glyph.
     *
     * @param plugin  the plugin instance
     * @param id      the unique identifier for the glyph
     * @param height  the height of the glyph in pixels
     * @param ascent  the ascent of the glyph in pixels
     * @param inChat  whether the glyph can be used in chat
     */
    public Glyph(JavaPlugin plugin, Identifier id, int height, int ascent, boolean inChat) {
        this.id = id;
        this.placeholder = ":" + id.toString() + ":";
        this.file = "glyphs/" + id.path();
        this.height = height;
        this.ascent = ascent;
        this.inChat = inChat;
        this.plugin = plugin;

        register(this);
    }

    public Identifier id() { return id; }
    public String placeholder() { return placeholder; }
    public String file() { return file; }
    public int height() { return height; }
    public int ascent() { return ascent; }
    public boolean inChat() { return inChat; }
    public char unicode() { return unicode; }
    public void unicode(char unicode) {
        this.unicode = unicode;
    }

    /**
     * Registers a glyph, adding it to the placeholder map and assigning a unique Unicode
     * .
     * @param glyph  the glyph to register
     */
    public static void register(Glyph glyph) {
        if (PLACEHOLDER_MAP.containsKey(glyph.placeholder())) {
            AbyssalLib.getInstance().getLogger().severe("Placeholder already registered: " + glyph.placeholder());
            return;
        }

        char unicode = nextUnicode++;
        while (unicode >= '\uF801' && unicode <= '\uF830') {
            unicode = nextUnicode++;
        }
        glyph.unicode(unicode);

        if (glyph.inChat()) {
            CHAT_MAP.put(glyph.placeholder(), glyph.unicode());
        }
        PLACEHOLDER_MAP.put(glyph.placeholder(), glyph.unicode());
        REGISTERED_GLYPHS.add(glyph);

        GlyphWriter.registerGlyph(glyph);
    }

    /**
     * Replaces all registered placeholders in the provided text with their Unicode characters.
     *
     * @param text the text to replace placeholders in
     * @return the text with placeholders replaced by their Unicode characters
     */
    public static String replacePlaceholders(String text) {
        for (Map.Entry<String, Character> entry : PLACEHOLDER_MAP.entrySet()) {
            text = text.replace(entry.getKey(), String.valueOf(entry.getValue()));
        }
        return text;
    }

    /**
     * Gets the map of all registered placeholders to Unicode characters.
     *
     * @return a map of placeholders to Unicode characters
     */
    public static Map<String, Character> getPlaceholderMap() {
        return PLACEHOLDER_MAP;
    }

    /**
     * Gets the map of chat-specific placeholders to Unicode characters.
     *
     * @return a map of chat placeholders to Unicode characters
     */
    public static Map<String, Character> getChatMap() {
        return CHAT_MAP;
    }

    /**
     * Gets the list of all registered glyphs.
     *
     * @return a list of all registered glyphs
     */
    public static List<Glyph> getGlyphs() {
        return List.copyOf(REGISTERED_GLYPHS);
    }
}