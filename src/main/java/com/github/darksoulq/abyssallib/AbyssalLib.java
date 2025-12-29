package com.github.darksoulq.abyssallib;

import com.github.darksoulq.abyssallib.common.config.internal.PluginConfig;
import com.github.darksoulq.abyssallib.common.energy.EnergyNetwork;
import com.github.darksoulq.abyssallib.common.util.FileUtils;
import com.github.darksoulq.abyssallib.common.util.Metrics;
import com.github.darksoulq.abyssallib.server.bridge.BlockBridge;
import com.github.darksoulq.abyssallib.server.bridge.ItemBridge;
import com.github.darksoulq.abyssallib.server.chat.ChatInputHandler;
import com.github.darksoulq.abyssallib.server.data.Datapack;
import com.github.darksoulq.abyssallib.server.event.EventBus;
import com.github.darksoulq.abyssallib.server.event.internal.*;
import com.github.darksoulq.abyssallib.server.resource.Namespace;
import com.github.darksoulq.abyssallib.server.resource.PackServer;
import com.github.darksoulq.abyssallib.server.resource.ResourcePack;
import com.github.darksoulq.abyssallib.server.resource.asset.Lang;
import com.github.darksoulq.abyssallib.server.resource.asset.Model;
import com.github.darksoulq.abyssallib.server.resource.asset.Texture;
import com.github.darksoulq.abyssallib.server.resource.asset.definition.Selector;
import com.github.darksoulq.abyssallib.server.resource.util.TextOffset;
import com.github.darksoulq.abyssallib.server.util.HookConstants;
import com.github.darksoulq.abyssallib.world.block.internal.BlockManager;
import com.github.darksoulq.abyssallib.world.entity.DamageType;
import com.github.darksoulq.abyssallib.world.gui.GuiManager;
import com.github.darksoulq.abyssallib.world.gui.internal.GuiTextures;
import com.github.darksoulq.abyssallib.world.item.Items;
import com.github.darksoulq.abyssallib.world.item.component.Components;
import com.github.darksoulq.abyssallib.world.item.internal.ItemTicker;
import com.github.darksoulq.abyssallib.world.multiblock.internal.MultiblockManager;
import com.github.darksoulq.abyssallib.world.recipe.RecipeLoader;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public final class AbyssalLib extends JavaPlugin {
    public static final String PLUGIN_ID = "abyssallib";
    private static AbyssalLib INSTANCE;
    public static Logger LOGGER;

    public static PluginConfig CONFIG;
    public static PackServer PACK_SERVER;
    public static EventBus EVENT_BUS;
    public static DamageType.Registrar DAMAGE_TYPE_REGISTRAR;
    public static Datapack.Registrar DATAPACK_REGISTRAR;

    @Override
    public void onEnable() {
        INSTANCE = this;
        LOGGER = getLogger();
        HookConstants.load();

        ItemBridge.setup();
        BlockBridge.setup();
        Components.DATA_COMPONENTS_VANILLA.apply();
        Components.DATA_COMPONENTS.apply();
        Items.ITEMS.apply();

        CONFIG = new PluginConfig();
        CONFIG.cfg.save();

        FileUtils.createDirectories(new File(getDataFolder(), "recipes"));
        RecipeLoader.loadFolder(new File(AbyssalLib.getInstance().getDataFolder(), "recipes"));

        EVENT_BUS = new EventBus(this);

        EVENT_BUS.register(new ChatInputHandler());
        EVENT_BUS.register(new PlayerEvents());
        EVENT_BUS.register(new EntityEvents());
        EVENT_BUS.register(new BlockEvents());
        EVENT_BUS.register(new MultiblockEvents());
        EVENT_BUS.register(new ItemEvents());
        EVENT_BUS.register(new ServerEvents());
        EVENT_BUS.register(new GuiEvents());

        GuiManager.init(this);
        ItemTicker.start();
        EnergyNetwork.init();

        PACK_SERVER = new PackServer();
        if (CONFIG.rp.enabled.get()) {
            EVENT_BUS.register(new PackEvent());
            PACK_SERVER.start(CONFIG.rp.ip.get(), CONFIG.rp.port.get());
        }

        if (CONFIG.metrics.get()) {
            new Metrics(this, 25772);
        }

        ResourcePack rp = new ResourcePack(this, PLUGIN_ID);
        Namespace ns = rp.namespace("abyssallib");
        ns.icon();

        TextOffset.init(ns);
        GuiTextures.init(ns);
        createItemDef("invisible", ns);
        createItemDef("forward", ns);
        createItemDef("backward", ns);

        Lang lang = ns.lang("en_us", false);
        lang.put("item.abyssallib.invisible", "");
        lang.put("item.abyssallib.forward", "Forward");
        lang.put("item.abyssallib.backward", "Backward");
        lang.put("plugin.abyssallib", "AbyssalLib");

        rp.register(false);
    }

    @Override
    public void onDisable() {
        BlockManager.save();
        MultiblockManager.save();
        EnergyNetwork.save();
        if (PACK_SERVER.isEnabled()) {
            PACK_SERVER.stop();
        }
    }

    public static AbyssalLib getInstance() {
        return INSTANCE;
    }

    private void createItemDef(String name, Namespace ns) {
        Texture tex = ns.texture("item/" + name);
        Model model = ns.model(name, false);
        model.parent("minecraft:item/generated");
        model.texture("layer0", tex);

        Selector.Model sel = new Selector.Model(model);
        ns.itemDefinition(name, sel, false);
    }

}
