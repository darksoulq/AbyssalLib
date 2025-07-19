package com.github.darksoulq.abyssallib.server.resource.util;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.resource.Namespace;
import com.github.darksoulq.abyssallib.server.resource.asset.Font;
import com.github.darksoulq.abyssallib.server.resource.asset.Lang;
import com.github.darksoulq.abyssallib.server.resource.asset.Texture;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

/**
 * Utility for generating invisible font-based glyph offsets to fine-tune horizontal text alignment.
 */
public class TextOffset {
    private static int[] widths = {8192, 4096, 2048, 1024, 512, 256, 128, 64, 32, 16, 8, 4, 2, 1};

    private static Texture SPLITTER;

    public static Font.TextureGlyph SPACE_P_1;
    public static Font.TextureGlyph SPACE_P_2;
    public static Font.TextureGlyph SPACE_P_4;
    public static Font.TextureGlyph SPACE_P_8;
    public static Font.TextureGlyph SPACE_P_16;
    public static Font.TextureGlyph SPACE_P_32;
    public static Font.TextureGlyph SPACE_P_64;
    public static Font.TextureGlyph SPACE_P_128;
    public static Font.TextureGlyph SPACE_P_256;
    public static Font.TextureGlyph SPACE_P_512;
    public static Font.TextureGlyph SPACE_P_1024;
    public static Font.TextureGlyph SPACE_P_2048;
    public static Font.TextureGlyph SPACE_P_4096;
    public static Font.TextureGlyph SPACE_P_8192;

    public static Font.TextureGlyph SPACE_N_1;
    public static Font.TextureGlyph SPACE_N_2;
    public static Font.TextureGlyph SPACE_N_4;
    public static Font.TextureGlyph SPACE_N_8;
    public static Font.TextureGlyph SPACE_N_16;
    public static Font.TextureGlyph SPACE_N_32;
    public static Font.TextureGlyph SPACE_N_64;
    public static Font.TextureGlyph SPACE_N_128;
    public static Font.TextureGlyph SPACE_N_256;
    public static Font.TextureGlyph SPACE_N_512;
    public static Font.TextureGlyph SPACE_N_1024;
    public static Font.TextureGlyph SPACE_N_2048;
    public static Font.TextureGlyph SPACE_N_4096;
    public static Font.TextureGlyph SPACE_N_8192;
    /**
     * Initializes the offset font glyphs and lang.
     *
     * @param ns The namespace
     */
    @ApiStatus.Internal
    public static void init(Namespace ns) {
        Font fn = ns.font("offset", false);
        SPLITTER = ns.texture("offset/split");

        SPACE_P_1 = fn.glyph(SPLITTER, 1, -5000);
        SPACE_P_2 = fn.glyph(SPLITTER, 2, -5000);
        SPACE_P_4 = fn.glyph(SPLITTER, 4, -5000);
        SPACE_P_8 = fn.glyph(SPLITTER, 8, -5000);
        SPACE_P_16 = fn.glyph(SPLITTER, 16, -5000);
        SPACE_P_32 = fn.glyph(SPLITTER, 32, -5000);
        SPACE_P_64 = fn.glyph(SPLITTER, 64, -5000);
        SPACE_P_128 = fn.glyph(SPLITTER, 128, -5000);
        SPACE_P_256 = fn.glyph(SPLITTER, 256, -5000);
        SPACE_P_512 = fn.glyph(SPLITTER, 512, -5000);
        SPACE_P_1024 = fn.glyph(SPLITTER, 1024, -5000);
        SPACE_P_2048 = fn.glyph(SPLITTER, 2048, -5000);
        SPACE_P_4096 = fn.glyph(SPLITTER, 4896, -5000);
        SPACE_P_8192 = fn.glyph(SPLITTER, 8192, -5000);

        SPACE_N_1 = fn.glyph(SPLITTER, -1, -5000);
        SPACE_N_2 = fn.glyph(SPLITTER, -2, -5000);
        SPACE_N_4 = fn.glyph(SPLITTER, -4, -5000);
        SPACE_N_8 = fn.glyph(SPLITTER, -8, -5000);
        SPACE_N_16 = fn.glyph(SPLITTER, -16, -5000);
        SPACE_N_32 = fn.glyph(SPLITTER, -32, -5000);
        SPACE_N_64 = fn.glyph(SPLITTER, -64, -5000);
        SPACE_N_128 = fn.glyph(SPLITTER, -128, -5000);
        SPACE_N_256 = fn.glyph(SPLITTER, -256, -5000);
        SPACE_N_512 = fn.glyph(SPLITTER, -512, -5000);
        SPACE_N_1024 = fn.glyph(SPLITTER, -1024, -5000);
        SPACE_N_2048 = fn.glyph(SPLITTER, -2048, -5000);
        SPACE_N_4096 = fn.glyph(SPLITTER, -4096, -5000);
        SPACE_N_8192 = fn.glyph(SPLITTER, -8192, -5000);

        // Spaces
        fn.offset(1, SPACE_P_1.character());
        fn.offset(2, SPACE_P_2.character());
        fn.offset(4, SPACE_P_4.character());
        fn.offset(8, SPACE_P_8.character());
        fn.offset(16, SPACE_P_16.character());
        fn.offset(32, SPACE_P_32.character());
        fn.offset(64, SPACE_P_64.character());
        fn.offset(128, SPACE_P_128.character());
        fn.offset(256, SPACE_P_256.character());
        fn.offset(512, SPACE_P_512.character());
        fn.offset(1024, SPACE_P_1024.character());
        fn.offset(2048, SPACE_P_2048.character());
        fn.offset(4096, SPACE_P_4096.character());
        fn.offset(8192, SPACE_P_8192.character());

        fn.offset(-1, SPACE_N_1.character());
        fn.offset(-2, SPACE_N_2.character());
        fn.offset(-4, SPACE_N_4.character());
        fn.offset(-8, SPACE_N_8.character());
        fn.offset(-16, SPACE_N_16.character());
        fn.offset(-32, SPACE_N_32.character());
        fn.offset(-64, SPACE_N_64.character());
        fn.offset(-128, SPACE_N_128.character());
        fn.offset(-256, SPACE_N_256.character());
        fn.offset(-512, SPACE_N_512.character());
        fn.offset(-1024, SPACE_N_1024.character());
        fn.offset(-2048, SPACE_N_2048.character());
        fn.offset(-4096, SPACE_N_4096.character());
        fn.offset(-8192, SPACE_N_8192.character());
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

        for (int width : widths) {
            while (Math.abs(remaining) >= width) {
                if (remaining > 0) {
                    sb.append(getPositiveGlyph(width).toMiniMessageString());
                    remaining -= width;
                } else {
                    sb.append(getNegativeGlyph(width).toMiniMessageString());
                    remaining += width;
                }
            }
        }

        return sb.toString();
    }

    private static Font.TextureGlyph getPositiveGlyph(int width) {
        return switch (width) {
            case 1 -> SPACE_P_1;
            case 2 -> SPACE_P_2;
            case 4 -> SPACE_P_4;
            case 8 -> SPACE_P_8;
            case 16 -> SPACE_P_16;
            case 32 -> SPACE_P_32;
            case 64 -> SPACE_P_64;
            case 128 -> SPACE_P_128;
            case 256 -> SPACE_P_256;
            case 512 -> SPACE_P_512;
            case 1024 -> SPACE_P_1024;
            case 2048 -> SPACE_P_2048;
            case 4096 -> SPACE_P_4096;
            case 8192 -> SPACE_P_8192;
            default -> throw new IllegalArgumentException("Unsupported positive width: " + width);
        };
    }

    private static Font.TextureGlyph getNegativeGlyph(int width) {
        return switch (width) {
            case 1 -> SPACE_N_1;
            case 2 -> SPACE_N_2;
            case 4 -> SPACE_N_4;
            case 8 -> SPACE_N_8;
            case 16 -> SPACE_N_16;
            case 32 -> SPACE_N_32;
            case 64 -> SPACE_N_64;
            case 128 -> SPACE_N_128;
            case 256 -> SPACE_N_256;
            case 512 -> SPACE_N_512;
            case 1024 -> SPACE_N_1024;
            case 2048 -> SPACE_N_2048;
            case 4096 -> SPACE_N_4096;
            case 8192 -> SPACE_N_8192;
            default -> throw new IllegalArgumentException("Unsupported negative width: " + width);
        };
    }

}
