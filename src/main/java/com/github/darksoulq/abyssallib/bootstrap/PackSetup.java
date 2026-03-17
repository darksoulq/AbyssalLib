package com.github.darksoulq.abyssallib.bootstrap;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.event.internal.PackEvent;
import com.github.darksoulq.abyssallib.server.resource.Namespace;
import com.github.darksoulq.abyssallib.server.resource.PackServer;
import com.github.darksoulq.abyssallib.server.resource.ResourcePack;
import com.github.darksoulq.abyssallib.server.resource.asset.Model;
import com.github.darksoulq.abyssallib.server.resource.asset.Texture;
import com.github.darksoulq.abyssallib.server.resource.asset.definition.Selector;
import com.github.darksoulq.abyssallib.server.resource.util.TextOffset;
import com.github.darksoulq.abyssallib.world.gui.internal.GuiTextures;

public final class PackSetup {

    public static void init(AbyssalLib plugin) {
        AbyssalLib.PACK_SERVER = new PackServer();

        if (AbyssalLib.CONFIG.rp.enabled.get()) {
            AbyssalLib.EVENT_BUS.register(new PackEvent());
            AbyssalLib.PACK_SERVER.start(
                AbyssalLib.CONFIG.rp.protocol.get(),
                AbyssalLib.CONFIG.rp.ip.get(),
                AbyssalLib.CONFIG.rp.port.get()
            );
        }

        createDefaultPack(plugin);
    }

    private static void createDefaultPack(AbyssalLib plugin) {
        ResourcePack rp = new ResourcePack(plugin, AbyssalLib.PLUGIN_ID);
        Namespace ns = rp.namespace(AbyssalLib.PLUGIN_ID);
        ns.icon();

        TextOffset.init(ns);
        GuiTextures.init(ns);

        createItemDef("invisible", ns);
        createItemDef("forward", ns);
        createItemDef("backward", ns);
        createItemDef("close", ns);
        createItemDef("back", ns);
        createItemDef("checkmark", ns);

        createItemDef("permission", ns);
        createItemDef("permission_bukkit", ns);
        createItemDef("perm_user", ns);
        createItemDef("perm_group", ns);

        createItemDef("bounding_toggle", ns);
        createItemDef("name_structure", ns);
        createItemDef("integrity", ns);
        createItemDef("load_structure", ns);
        createItemDef("mirror", ns);
        createItemDef("rotate", ns);
        createItemDef("save", ns);
        createItemDef("size_x", ns);
        createItemDef("size_y", ns);
        createItemDef("size_z", ns);
        createItemDef("x", ns);
        createItemDef("y", ns);
        createItemDef("z", ns);

        rp.register(false);
    }

    private static void createItemDef(String name, Namespace ns) {
        Texture tex = ns.texture("item/" + name);
        Model model = ns.model(name, false);
        model.parent("minecraft:item/generated");
        model.texture("layer0", tex);

        Selector.Model sel = new Selector.Model(model);
        ns.itemDefinition(name, sel, false);
    }
}