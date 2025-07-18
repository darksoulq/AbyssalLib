package com.github.darksoulq.abyssallib.server.resource.util;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.resource.Namespace;
import com.github.darksoulq.abyssallib.server.resource.asset.Font;
import com.github.darksoulq.abyssallib.server.resource.asset.Lang;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.ApiStatus;

/**
 * Utility for generating invisible font-based glyph offsets to fine-tune horizontal text alignment.
 */
public class TextOffset {
    private static int[] widths = {8192, 4096, 2048, 1024, 512, 256, 128, 64, 32, 16, 8, 4, 2, 1};

    /**
     * Initializes the offset font glyphs and lang.
     *
     * @param ns The namespace
     */
    @ApiStatus.Internal
    public static void init(Namespace ns) {
        ns.font("offset", true);
        ns.lang("en_us", true);
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
        for (int width : widths) {
            sb.append("<font:abyssallib:offset>");

            int remaining = pixelOffset;

            while (Math.abs(remaining) >= width) {
                sb.append("<lang:space.").append(remaining > 0 ? width : -width).append(">");
                remaining += remaining > 0 ? -width : width;
            }
            sb.append("</font>");
        }
        return sb.toString();
    }

}
