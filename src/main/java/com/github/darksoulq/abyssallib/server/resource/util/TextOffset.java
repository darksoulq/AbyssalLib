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
 * <p>
 * This class uses a custom font provider to register glyphs with specific advance widths.
 * By appending these characters to a string, text following them is shifted horizontally
 * without rendering any visible characters.
 */
public class TextOffset {

    /**
     * An array of prime and strategic pixel widths used to calculate any arbitrary offset.
     */
    private static final int[] widths = {
        1, 2, 3, 5, 7, 11, 17, 23, 29, 37, 53, 67, 89, 113, 151, 199, 257, 331, 419
    };

    /**
     * Internal mapping of pixel widths to their corresponding positive-shifting glyphs.
     */
    private static final Map<Integer, Font.TextureGlyph> positiveGlyphs = new HashMap<>();

    /**
     * Internal mapping of pixel widths to their corresponding negative-shifting glyphs.
     */
    private static final Map<Integer, Font.TextureGlyph> negativeGlyphs = new HashMap<>();

    /**
     * Initializes the offset font provider and registers all necessary glyphs.
     * <p>
     * This method registers an "offset" font within the given namespace and creates
     * texture glyphs that are moved far off-screen (-9000 height) to ensure invisibility.
     *
     * @param ns The {@link Namespace} context where the font and textures will be registered.
     */
    @ApiStatus.Internal
    public static void init(Namespace ns) {
        Font font = ns.font("offset", false);
        Texture SPLITTER = ns.texture("offset/split");

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
     * @param pixelOffset The number of pixels to offset the text (positive for right, negative for left).
     * @return A {@link Component} containing the invisible offset glyphs in the custom font.
     */
    public static Component getOffset(int pixelOffset) {
        return MiniMessage.miniMessage().deserialize(getOffsetMinimessage(pixelOffset));
    }

    /**
     * Converts a pixel offset into a MiniMessage string containing invisible glyphs.
     * <p>
     * The method uses a greedy algorithm to decompose the target {@code pixelOffset}
     * into the fewest possible number of predefined glyph widths.
     *
     * @param pixelOffset The number of pixels to offset the text.
     * @return A MiniMessage-formatted {@link String} using the {@code abyssallib:offset} font.
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