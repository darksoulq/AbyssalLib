package com.github.darksoulq.abyssallib;

import com.github.darksoulq.abyssallib.server.chat.ChatInputHandler;
import com.github.darksoulq.abyssallib.server.config.Config;
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
    public static EventBus EVENT_BUS;
    public static DamageType.Registrar DAMAGE_TYPE_REGISTRAR;
    public static Datapack.Registrar DATAPACK_REGISTRAR;

    @Override
    public void onEnable() {
        InternalConfig.setup();
        INSTANCE = this;
        isRPManagerInstalled = checkRPManager();

        new BukkitRunnable() {
            @Override
            public void run() {
                int saved = BlockManager.save();
                getLogger().info("Saved " + saved + " blocks");
            }
        }.runTaskTimerAsynchronously(this, 20L * 60 * 2, 20L * 60 * 5);

        EVENT_BUS = new EventBus(this);

        EVENT_BUS.register(new ChatInputHandler());
        EVENT_BUS.register(new PlayerEvents());
        EVENT_BUS.register(new BlockEvents());
        EVENT_BUS.register(new ItemEvents());
        EVENT_BUS.register(new ServerEvents());
        EVENT_BUS.register(new GuiEvents());

        GuiManager.init(this);

        BuiltinTags.TAGS.apply();

        if (InternalConfig.RESOURCEPACK_ENABLED.get()) {
            EVENT_BUS.register(new PackEvent());
            PACK_SERVER = new PackServer();
            PACK_SERVER.start(InternalConfig.RESOURCEPACK_HOST.get(), InternalConfig.RESOURCEPACK_PORT.get());
        }

        if (InternalConfig.METRICS_ENABLED.get()) {
            new Metrics(this, 25772);
        }

        if (InternalConfig.FEATURES_CUSTOM_BLOCK_BREAKING.get()) {
            EVENT_BUS.register(new CustomBlockBreak());
        }

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

    private boolean checkRPManager() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("ResourcePackManager");
        return plugin != null && plugin.isEnabled();
    }

    public static AbyssalLib getInstance() {
        return INSTANCE;
    }

    public static class InternalConfig {
        public static Config.Value<Boolean> RESOURCEPACK_ENABLED;
        public static Config.Value<String> RESOURCEPACK_HOST;
        public static Config.Value<Integer> RESOURCEPACK_PORT;
        public static Config.Value<Boolean> METRICS_ENABLED;
        public static Config.Value<Boolean> FEATURES_CUSTOM_BLOCK_BREAKING;

        public static void setup() {
            Config config = new Config("config.yml", "abyssallib", "");

            METRICS_ENABLED = new Config.Value<>(config, "metrics.enabled", true);
            RESOURCEPACK_ENABLED = new Config.Value<>(config, "resource_pack.enabled", false);
            RESOURCEPACK_HOST = new Config.Value<>(config, "resource_pack.host", "127.0.0.1");
            RESOURCEPACK_PORT = new Config.Value<>(config, "resource_pack.port", 8080);
            FEATURES_CUSTOM_BLOCK_BREAKING = new Config.Value<>(config, "features.custom_block_breaking", false);

            config.save();
        }
    }
}
