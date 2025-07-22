package com.github.darksoulq.abyssallib.server.resource.util;

import com.github.darksoulq.abyssallib.server.resource.Namespace;
import com.github.darksoulq.abyssallib.server.resource.asset.Font;
import com.github.darksoulq.abyssallib.server.resource.asset.Texture;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility for generating invisible font-based glyph offsets to fine-tune horizontal text alignment.
 */
public class TextOffset {
    private static final int[] widths = {
            1, 2, 3, 5, 7, 11, 17, 23, 29, 37, 53, 67, 89, 113, 151, 199, 257, 331, 419
    };

    private static Texture SPLITTER;

    private static Font font;

    private static final Map<Integer, Font.TextureGlyph> positiveGlyphs = new HashMap<>();
    private static final Map<Integer, Font.TextureGlyph> negativeGlyphs = new HashMap<>();

    /**
     * Initializes the offset font glyphs and lang.
     *
     * @param ns The namespace
     */
    @ApiStatus.Internal
    public static void init(Namespace ns) {
        font = ns.font("offset", false);
        SPLITTER = ns.texture("offset/split");

        for (int width : widths) {
            positiveGlyphs.put(width, font.glyph(SPLITTER, width, -9000));
            negativeGlyphs.put(width, font.glyph(SPLITTER, -width, -9000));

            font.offset(width, positiveGlyphs.get(width).character());
            font.offset(-width, negativeGlyphs.get(width).character());
        }
    }

    /**
     * Converts a pixel offset into a sequence of invisible glyphs that shift text left or right.
     *
     * @param pixelOffset The number of pixels to offset the text (can be negative or positive).
     * @return A {@link Component} made of offset glyphs, using the correct font namespace.
     */
    public static Component getOffset(int pixelOffset) {
        return MiniMessage.miniMessage().deserialize(getOffsetMinimessage(pixelOffset));
    }

    /**
     * Converts a pixel offset into a sequence of invisible glyphs that shift text left or right.
     *
     * @param pixelOffset The number of pixels to offset the text (can be negative or positive).
     * @return A Minimessage {@link String} made of offset glyphs, using the correct font namespace.
     */
    public static String getOffsetMinimessage(int pixelOffset) {
        if (pixelOffset == 0) return "";

        StringBuilder sb = new StringBuilder();
        int remaining = pixelOffset;

        for (int i = widths.length - 1; i >= 0; i--) {
            int width = widths[i];
            while (Math.abs(remaining) >= width) {
                if (remaining > 0) {
                    sb.append(positiveGlyphs.get(width).character());
                    remaining -= width;
                } else {
                    sb.append(negativeGlyphs.get(width).character());
                    remaining += width;
                }
            }
        }

        return "<font:abyssallib:offset>" + sb + "</font>";
    }
}
