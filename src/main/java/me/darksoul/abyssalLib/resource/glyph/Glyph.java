package me.darksoul.abyssalLib.resource.glyph;

import me.darksoul.abyssalLib.AbyssalLib;
import me.darksoul.abyssalLib.util.ResourceLocation;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Glyph {
    private static final Map<String, Character> CHAT_MAP = new HashMap<>();
    private static final Map<String, Character> PLACEHOLDER_MAP = new HashMap<>();
    private static final List<Glyph> REGISTERED_GLYPHS = new ArrayList<>();
    private static char nextUnicode = '\ue000';

    private final ResourceLocation id;
    private final String placeholder;
    private final boolean inChat;
    private final String file;
    private final int height;
    private final int ascent;
    private char unicode;

    public Glyph(JavaPlugin plugin, ResourceLocation id, int height, int ascent, boolean inChat) {
        this.id = id;
        this.placeholder = ":" + id.toString() + ":";
        this.file = "glyphs/" + id.path();
        this.height = height;
        this.ascent = ascent;
        this.inChat = inChat;

        register(plugin, this);
    }

    public ResourceLocation id() { return id; }
    public String placeholder() { return placeholder; }
    public String file() { return file; }
    public int height() { return height; }
    public int ascent() { return ascent; }
    public boolean inChat() { return inChat; }
    public char unicode() { return unicode; }
    public void unicode(char unicode) {
        this.unicode = unicode;
    }

    public static void register(JavaPlugin plugin, Glyph glyph) {
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

        GlyphWriter.write(plugin, glyph);
    }

    public static String replacePlaceholders(String text) {
        for (Map.Entry<String, Character> entry : PLACEHOLDER_MAP.entrySet()) {
            text = text.replace(entry.getKey(), String.valueOf(entry.getValue()));
        }
        return text;
    }

    public static Map<String, Character> getPlaceholderMap() {
        return PLACEHOLDER_MAP;
    }

    public static Map<String, Character> getChatMap() {
        return CHAT_MAP;
    }

    public static List<Glyph> getGlyphs() {
        return List.copyOf(REGISTERED_GLYPHS);
    }
}
