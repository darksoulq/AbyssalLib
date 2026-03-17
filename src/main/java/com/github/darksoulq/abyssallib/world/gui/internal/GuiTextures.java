package com.github.darksoulq.abyssallib.world.gui.internal;

import com.github.darksoulq.abyssallib.server.resource.Namespace;
import com.github.darksoulq.abyssallib.server.resource.asset.Font;

public class GuiTextures {
    public static Font.TextureGlyph GENERIC_9X6_PAGE_MENU;
    public static Font.TextureGlyph STRUCTURE_BLOCK_MENU;
    public static Font.TextureGlyph PERMISSION_MAIN_MENU;
    public static Font.TextureGlyph PERMISSION_SEARCH_MENU;
    public static Font.TextureGlyph MOUSE_LEFT;
    public static Font.TextureGlyph MOUSE_RIGHT;

    public static void init(Namespace ns) {
        Font fn = ns.font("gui", false);
        GENERIC_9X6_PAGE_MENU = fn.glyph(ns.texture("gui/generic_9x6_page_menu"), 222, 13);
        STRUCTURE_BLOCK_MENU = fn.glyph(ns.texture("gui/structure_block_menu"), 222, 13);
        PERMISSION_MAIN_MENU = fn.glyph(ns.texture("gui/permission_main_menu"), 168, 13);
        PERMISSION_SEARCH_MENU = fn.glyph(ns.texture("gui/permission_search_menu"), 168, 13);
        MOUSE_LEFT = fn.glyph("mouse_left", ns.texture("gui/mouse_left"), 8, 8);
        MOUSE_RIGHT = fn.glyph(ns.texture("gui/mouse_right"), 8, 8);
    }
}
