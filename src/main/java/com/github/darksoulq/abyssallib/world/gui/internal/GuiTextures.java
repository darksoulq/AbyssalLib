package com.github.darksoulq.abyssallib.world.gui.internal;

import com.github.darksoulq.abyssallib.server.resource.Namespace;
import com.github.darksoulq.abyssallib.server.resource.asset.Font;

public class GuiTextures {
    public static Font.TextureGlyph ITEM_MAIN_MENU;

    public static void init(Namespace ns) {
        Font fn = ns.font("gui", false);
        ITEM_MAIN_MENU = fn.glyph(ns.texture("gui/item_menu_main"), 222, 13);
    }
}
