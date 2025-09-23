package com.github.darksoulq.abyssallib;

import com.github.darksoulq.abyssallib.common.config.internal.PluginConfig;
import com.github.darksoulq.abyssallib.common.util.FileUtils;
import com.github.darksoulq.abyssallib.common.util.Metrics;
import com.github.darksoulq.abyssallib.server.HookConstants;
import com.github.darksoulq.abyssallib.server.bridge.ItemBridge;
import com.github.darksoulq.abyssallib.server.chat.ChatInputHandler;
import com.github.darksoulq.abyssallib.server.data.Datapack;
import com.github.darksoulq.abyssallib.server.event.EventBus;
import com.github.darksoulq.abyssallib.server.event.internal.*;
import com.github.darksoulq.abyssallib.server.resource.Namespace;
import com.github.darksoulq.abyssallib.server.resource.PackServer;
import com.github.darksoulq.abyssallib.server.resource.ResourcePack;
import com.github.darksoulq.abyssallib.server.resource.asset.ItemDefinition;
import com.github.darksoulq.abyssallib.server.resource.asset.Model;
import com.github.darksoulq.abyssallib.server.resource.asset.Texture;
import com.github.darksoulq.abyssallib.server.resource.asset.definition.Selector;
import com.github.darksoulq.abyssallib.server.resource.util.TextOffset;
import com.github.darksoulq.abyssallib.world.block.internal.BlockManager;
import com.github.darksoulq.abyssallib.world.data.tag.BuiltinTags;
import com.github.darksoulq.abyssallib.world.entity.DamageType;
import com.github.darksoulq.abyssallib.world.gui.GuiManager;
import com.github.darksoulq.abyssallib.world.item.Items;
import com.github.darksoulq.abyssallib.world.item.component.Components;
import com.github.darksoulq.abyssallib.world.recipe.VanillaRecipeLoader;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

public final class AbyssalLib extends JavaPlugin {

    public static final String MODID = "abyssallib";
    private static AbyssalLib INSTANCE;

    public static PluginConfig CONFIG;
    public static PackServer PACK_SERVER;
    public static EventBus EVENT_BUS;
    public static DamageType.Registrar DAMAGE_TYPE_REGISTRAR;
    public static Datapack.Registrar DATAPACK_REGISTRAR;

    @Override
    public void onEnable() {
        INSTANCE = this;
        HookConstants.load();

        ItemBridge.setup();

        new BukkitRunnable() {
            @Override
            public void run() {
                int saved = BlockManager.save();
                getLogger().info("Saved " + saved + " blocks");
            }
        }.runTaskTimerAsynchronously(this, 20L * 60 * 2, 20L * 60 * 5);

        CONFIG = new PluginConfig();
        CONFIG.cfg.save();

        FileUtils.createDirectories(new File(getDataFolder(), "recipes"));
        VanillaRecipeLoader.loadFolder(new File(getDataFolder(), "recipes"));

        EVENT_BUS = new EventBus(this);

        EVENT_BUS.register(new ChatInputHandler());
        EVENT_BUS.register(new PlayerEvents());
        EVENT_BUS.register(new BlockEvents());
        EVENT_BUS.register(new ItemEvents());
        EVENT_BUS.register(new ServerEvents());
        EVENT_BUS.register(new GuiEvents());

        GuiManager.init(this);

        BuiltinTags.TAGS.apply();

        if (CONFIG.rp.enabled.get()) {
            EVENT_BUS.register(new PackEvent());
            PACK_SERVER = new PackServer();
            PACK_SERVER.start(CONFIG.rp.ip.get(), CONFIG.rp.port.get());
        }

        if (CONFIG.metrics.get()) {
            new Metrics(this, 25772);
        }

        Components.DATA_COMPONENTS_VANILLA.apply();
        Components.DATA_COMPONENTS.apply();
        Items.ITEMS.apply();

        ResourcePack rp = new ResourcePack(this, MODID);
        Namespace ns = rp.namespace("abyssallib");

        TextOffset.init(ns);

        Texture invisTexture = ns.texture("item/invis");
        Model invisModel = ns.model("invis", false);
        invisModel.parent("minecraft:item/generated");
        invisModel.texture("layer0", invisTexture);

        Selector.Model invisSelector = new Selector.Model(invisModel);
        ItemDefinition def = ns.itemDefinition("invisible", invisSelector, false);

        rp.register(false);
    }

    @Override
    public void onDisable() {
        BlockManager.save();
        if (PACK_SERVER != null) {
            PACK_SERVER.stop();
        }
    }
    public static AbyssalLib getInstance() {
        return INSTANCE;
    }
}
