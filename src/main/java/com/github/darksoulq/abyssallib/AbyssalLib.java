package com.github.darksoulq.abyssallib;

import com.github.darksoulq.abyssallib.server.chat.ChatInputHandler;
import com.github.darksoulq.abyssallib.server.config.legacy.Config;
import com.github.darksoulq.abyssallib.server.config.legacy.ConfigSpec;
import com.github.darksoulq.abyssallib.server.data.Datapack;
import com.github.darksoulq.abyssallib.server.event.EventBus;
import com.github.darksoulq.abyssallib.server.event.internal.*;
import com.github.darksoulq.abyssallib.server.packet.internal.BlockBreakListener;
import com.github.darksoulq.abyssallib.server.resource.Namespace;
import com.github.darksoulq.abyssallib.server.resource.PackServer;
import com.github.darksoulq.abyssallib.server.resource.ResourcePack;
import com.github.darksoulq.abyssallib.server.resource.asset.ItemDefinition;
import com.github.darksoulq.abyssallib.server.resource.asset.Model;
import com.github.darksoulq.abyssallib.server.resource.asset.Texture;
import com.github.darksoulq.abyssallib.server.resource.asset.definition.Selector;
import com.github.darksoulq.abyssallib.server.resource.util.TextOffset;
import com.github.darksoulq.abyssallib.util.Metrics;
import com.github.darksoulq.abyssallib.world.level.block.internal.BlockManager;
import com.github.darksoulq.abyssallib.world.level.data.tag.BuiltinTags;
import com.github.darksoulq.abyssallib.world.level.entity.DamageType;
import com.github.darksoulq.abyssallib.world.level.inventory.gui.GuiManager;
import com.github.darksoulq.abyssallib.world.level.item.Items;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class AbyssalLib extends JavaPlugin {

    public static final String MODID = "abyssallib";
    private static AbyssalLib INSTANCE;

    public static boolean isRPManagerInstalled = false;
    public static PackServer PACK_SERVER;
    public static GuiManager GUI_MANAGER;
    public static ConfigSpec CONFIG;
    public static ChatInputHandler CHAT_INPUT_HANDLER;
    public static EventBus EVENT_BUS;
    public static DamageType.Registrar DAMAGE_TYPE_REGISTRAR;
    public static Datapack.Registrar DATAPACK_REGISTRAR;

    @Override
    public void onEnable() {
        INSTANCE = this;
        isRPManagerInstalled = checkRPManager();

        new BukkitRunnable() {
            @Override
            public void run() {
                int saved = BlockManager.INSTANCE.save();
                getLogger().info("Saved " + saved + " blocks");
            }
        }.runTaskTimerAsynchronously(this, 0, 20L * 60 * 5);

        GUI_MANAGER = new GuiManager();
        CHAT_INPUT_HANDLER = new ChatInputHandler();
        EVENT_BUS = new EventBus(this);

        EVENT_BUS.register(CHAT_INPUT_HANDLER);
        EVENT_BUS.register(new PlayerEvents());
        EVENT_BUS.register(new BlockEvents());
        EVENT_BUS.register(new ItemEvents());
        EVENT_BUS.register(new ServerEvents());
        EVENT_BUS.register(new GuiEvents());

        new BlockBreakListener().register();

        BuiltinTags.TAGS.apply();

        CONFIG = new ConfigSpec();
        CONFIG.define(ConfigSpec.ConfigType.BOOLEAN, "resource-pack.autohost", true);
        CONFIG.define(ConfigSpec.ConfigType.STRING, "resource-pack.ip", "127.0.0.1");
        CONFIG.define(ConfigSpec.ConfigType.INT, "resource-pack.port", 8080);
        CONFIG.define(ConfigSpec.ConfigType.BOOLEAN, "metrics.enabled", true);
        Config.register(MODID, CONFIG);

        if (CONFIG.getBoolean("resource-pack.autohost")) {
            EVENT_BUS.register(new PackEvent());
            PACK_SERVER = new PackServer();
            PACK_SERVER.start(CONFIG.getString("resource-pack.ip"), CONFIG.getInt("resource-pack.port"));
        }

        if (CONFIG.getBoolean("metrics.enabled")) {
            new Metrics(this, 25772);
        }

        Items.ITEMS.apply();

        ResourcePack rp = new ResourcePack(this, MODID);
        Namespace ns = rp.namespace("abyssallib");

        TextOffset.init(ns);

        Texture invisTexture = ns.texture("invis");
        Model invisModel = ns.model("invis", false);
        invisModel.parent("minecraft:item/generated");
        invisModel.texture("layer0", invisTexture);

        Selector invisSelector = new Selector.Model(invisModel);
        ItemDefinition def = ns.itemDefinition("invisible", invisSelector, false);

        rp.register(false);
    }

    @Override
    public void onDisable() {
        BlockManager.INSTANCE.save();
        if (PACK_SERVER != null) {
            PACK_SERVER.stop();
        }
    }

    private boolean checkRPManager() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("ResourcePackManager");
        return plugin != null && plugin.isEnabled();
    }

    public static AbyssalLib getInstance() {
        return INSTANCE;
    }
}
