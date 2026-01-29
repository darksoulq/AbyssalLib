package com.github.darksoulq.abyssallib.world.gui.internal;

import com.github.darksoulq.abyssallib.server.resource.Namespace;
import com.github.darksoulq.abyssallib.server.resource.asset.Font;

public class GuiTextures {
    public static Font.TextureGlyph ITEM_MAIN_MENU;
    public static Font.TextureGlyph STRUCTURE_BLOCK_MENU;
    public static Font.TextureGlyph MOUSE_LEFT;
    public static Font.TextureGlyph MOUSE_RIGHT;

    public static void init(Namespace ns) {
        Font fn = ns.font("gui", false);
        ITEM_MAIN_MENU = fn.glyph(ns.texture("gui/item_main_menu"), 222, 13);
        STRUCTURE_BLOCK_MENU = fn.glyph(ns.texture("gui/structure_block_menu"), 222, 13);
        MOUSE_LEFT = fn.glyph(ns.texture("gui/mouse_left"), 8, 8);
        MOUSE_RIGHT = fn.glyph(ns.texture("gui/mouse_right"), 8, 8);
    }
}
