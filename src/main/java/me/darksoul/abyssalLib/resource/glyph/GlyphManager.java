package me.darksoul.abyssalLib.resource.glyph;

import me.darksoul.abyssalLib.AbyssalLib;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlyphManager {
    private static final Map<String, Character> CHAT_MAP = new HashMap<>();
    private static final Map<String, Character> PLACEHOLDER_MAP = new HashMap<>();
    private static final List<Glyph> REGISTERED_GLYPHS = new ArrayList<>();
    private static char nextUnicode = '\ue000';

    public static void register(JavaPlugin plugin, Glyph glyph) {
        if (PLACEHOLDER_MAP.containsKey(glyph.placeholder())) {
            AbyssalLib.getInstance().getLogger().severe("Placeholder already registered: " + glyph.placeholder());
            return;
        }

        char unicode = nextUnicode++;
        glyph.unicode(unicode);

        if (glyph.inChat()) {
            CHAT_MAP.put(glyph.placeholder(), unicode);
        }
        PLACEHOLDER_MAP.put(glyph.placeholder(), glyph.unicode());
        REGISTERED_GLYPHS.add(glyph);

        GlyphWriter.write(plugin, glyph);
    }

    public static Map<String, Character> getChatMap() {
        return CHAT_MAP;
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

    public static List<Glyph> getGlyphs() {
        return List.copyOf(REGISTERED_GLYPHS);
    }
}
