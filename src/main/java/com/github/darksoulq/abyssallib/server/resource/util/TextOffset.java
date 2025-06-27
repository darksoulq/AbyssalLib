package com.github.darksoulq.abyssallib.server.resource.util;

import com.github.darksoulq.abyssallib.server.resource.Namespace;
import com.github.darksoulq.abyssallib.server.resource.asset.Font;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;

/**
 * Utility for generating invisible font-based glyph offsets to fine-tune horizontal text alignment.
 */
public class TextOffset {

    /** Invisible glyph that shifts text left by 1 pixel. */
    private static Font.OffsetGlyph PX_N_1;

    /** Invisible glyph that shifts text left by 10 pixels. */
    private static Font.OffsetGlyph PX_N_10;

    /** Invisible glyph that shifts text left by 100 pixels. */
    private static Font.OffsetGlyph PX_N_100;

    /** Invisible glyph that shifts text right by 1 pixel. */
    private static Font.OffsetGlyph PX_P_1;

    /** Invisible glyph that shifts text right by 10 pixels. */
    private static Font.OffsetGlyph PX_P_10;

    /** Invisible glyph that shifts text right by 100 pixels. */
    private static Font.OffsetGlyph PX_P_100;

    /**
     * Initializes the offset font glyphs for both positive and negative pixel shifts.
     *
     * @param ns The namespace where the offset font is registered.
     */
    @ApiStatus.Internal
    public static void init(Namespace ns) {
        Font OFFSET_FONT = ns.font("offset", false);

        PX_N_1 = OFFSET_FONT.offset(-1);
        PX_N_10 = OFFSET_FONT.offset(-10);
        PX_N_100 = OFFSET_FONT.offset(-100);

        PX_P_1 = OFFSET_FONT.offset(1);
        PX_P_10 = OFFSET_FONT.offset(10);
        PX_P_100 = OFFSET_FONT.offset(100);
    }

    /**
     * Converts a pixel offset into a sequence of invisible glyphs that shift text left or right.
     *
     * @param pixelOffset The number of pixels to offset the text (can be negative or positive).
     * @return A {@link Component} made of offset glyphs, using the correct font namespace.
     */
    public static Component getOffset(int pixelOffset) {
        if (pixelOffset == 0) return Component.empty();

        boolean negative = pixelOffset < 0;
        int abs = Math.abs(pixelOffset);

        int h = abs / 100;
        int t = (abs % 100) / 10;
        int o = abs % 10;

        Font.OffsetGlyph g100 = negative ? PX_N_100 : PX_P_100;
        Font.OffsetGlyph g10 = negative ? PX_N_10 : PX_P_10;
        Font.OffsetGlyph g1 = negative ? PX_N_1 : PX_P_1;

        StringBuilder sb = new StringBuilder(h + t + o);
        for (int i = 0; i < h; i++) sb.append(g100.character());
        for (int i = 0; i < t; i++) sb.append(g10.character());
        for (int i = 0; i < o; i++) sb.append(g1.character());

        return Component.text(sb.toString()).font(g1.fontId().toNamespace());
    }
}
