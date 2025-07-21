package com.github.darksoulq.abyssallib.server.resource.util;

import com.github.darksoulq.abyssallib.server.resource.Namespace;
import com.github.darksoulq.abyssallib.server.resource.asset.Font;
import com.github.darksoulq.abyssallib.server.resource.asset.Texture;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

public class TextOffset {

    private static final int MIN_OFFSET = -1000;
    private static final int MAX_OFFSET = 1000;

    private static final Map<Integer, Font.TextureGlyph> GLYPHS = new HashMap<>();
    private static Texture SPLITTER;

    @ApiStatus.Internal
    public static void init(Namespace ns) {
        Font font = ns.font("offset", false);
        SPLITTER = ns.texture("offset/split");

        for (int i = MIN_OFFSET; i <= MAX_OFFSET; i++) {
            if (i == 0) continue;

            Font.TextureGlyph glyph = font.glyph(SPLITTER, i, -5000);
            GLYPHS.put(i, glyph);
            font.offset(i, glyph.character());
        }
    }

    /**
     * Returns an Adventure {@link Component} representing the exact pixel offset,
     * using the registered glyphs. Only values from -1000 to +1000 (excluding 0) are allowed.
     *
     * @param pixelOffset pixel offset, must be in range [-1000, 1000] and not 0
     * @return invisible component used to shift text
     */
    public static Component getOffset(int pixelOffset) {
        return MiniMessage.miniMessage().deserialize(getOffsetMinimessage(pixelOffset));
    }

    /**
     * Returns a MiniMessage string that applies a text offset.
     * Only values from -1000 to +1000 (excluding 0) are allowed.
     *
     * @param pixelOffset pixel offset
     * @return minimessage string
     */
    public static String getOffsetMinimessage(int pixelOffset) {
        if (pixelOffset == 0) return "";

        Font.TextureGlyph glyph = GLYPHS.get(pixelOffset);
        if (glyph == null) {
            throw new IllegalArgumentException("Offset out of bounds: " + pixelOffset);
        }

        return glyph.toMiniMessageString();
    }
}
