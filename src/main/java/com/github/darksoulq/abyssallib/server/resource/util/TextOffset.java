package com.github.darksoulq.abyssallib.server.resource.util;

import com.github.darksoulq.abyssallib.server.resource.Namespace;
import com.github.darksoulq.abyssallib.server.resource.asset.Font;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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

    /** Internal Bitmap for glyphs **/
    private static @NotNull List<LinkedList<Font.Glyph>> GLYPHS = new ArrayList<>();

    /**
     * Initializes the offset font glyphs for both positive and negative pixel shifts.
     *
     * @param ns The namespace where the offset font is registered.
     */
    @ApiStatus.Internal
    public static void init(Namespace ns) {
        Font fn = ns.font("offset", false);

        GLYPHS = fn.glyphs(ns.texture("offset/transparent"), 8, 8, 8, 8);
        LinkedList<Font.Glyph> temp = GLYPHS.getFirst();
        for (int i = 0; i < temp.size(); i++) {
            Font.TextureGlyph g = (Font.TextureGlyph) temp.get(i);
            switch (i) {
                case 0 -> PX_N_1 = fn.offset(-1, g.character());
                case 1 -> PX_N_10 = fn.offset(-10, g.character());
                case 2 -> PX_N_100 = fn.offset(-100, g.character());
                case 3 -> PX_P_1 = fn.offset(1, g.character());
                case 4 -> PX_P_10 = fn.offset(10, g.character());
                case 5 -> PX_P_100 = fn.offset(100, g.character());
            }
        }
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
