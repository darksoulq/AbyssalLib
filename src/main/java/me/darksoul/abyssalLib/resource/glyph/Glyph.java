package me.darksoul.abyssalLib.resource.glyph;

import me.darksoul.abyssalLib.util.ResourceLocation;

public class Glyph {
    private final ResourceLocation id;
    private final String placeholder;
    private final boolean inChat;
    private final String file;
    private final int height;
    private final int ascent;
    private char unicode;

    public Glyph(ResourceLocation id, int height, int ascent, boolean inChat) {
        this.id = id;
        this.placeholder = "%" + id.toString() + "%";
        this.file = "glyphs/" + id.path();
        this.height = height;
        this.ascent = ascent;
        this.inChat = inChat;
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
}
