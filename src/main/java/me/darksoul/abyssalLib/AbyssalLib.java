package me.darksoul.abyssalLib;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.darksoul.abyssalLib.block.BlockManager;
import me.darksoul.abyssalLib.block.test.ModBlocks;
import me.darksoul.abyssalLib.command.CommandBus;
import me.darksoul.abyssalLib.config.Config;
import me.darksoul.abyssalLib.config.ConfigSpec;
import me.darksoul.abyssalLib.event.*;
import me.darksoul.abyssalLib.gui.GuiManager;
import me.darksoul.abyssalLib.item.test.TestItems;
import me.darksoul.abyssalLib.recipe.test.TestRecipes;
import me.darksoul.abyssalLib.registry.BuiltinRegistries;
import me.darksoul.abyssalLib.resource.PackServer;
import me.darksoul.abyssalLib.resource.ResourcePack;
import me.darksoul.abyssalLib.resource.glyph.Glyph;
import me.darksoul.abyssalLib.util.ChatInputHandler;
import me.darksoul.abyssalLib.util.ResourceLocation;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class AbyssalLib extends JavaPlugin {
    public static String MODID = "abyssallib";
    private static AbyssalLib INSTANCE;
    public static PackServer PACK_SERVER;
    public static GuiManager GUI_MANAGER;
    public static ConfigSpec CONFIG;
    public static ChatInputHandler CHAT_INPUT_HANDLER;

    public static boolean isRPManagerInstalled = false;

    @Override
    public void onEnable() {
        INSTANCE = this;
        isRPManagerInstalled = checkRPManager();

        BlockManager.INSTANCE.load();
        GUI_MANAGER = new GuiManager();
        CHAT_INPUT_HANDLER = new ChatInputHandler();
        EventBus bus = new EventBus(this);

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            CommandBus.init(commands.registrar().getDispatcher());
        });

        bus.register(GUI_MANAGER);
        bus.register(CHAT_INPUT_HANDLER);
        bus.register(new PlayerEvents());
        bus.register(new BlockEvents());
        bus.register(new ItemEvents());
        bus.register(new ServerEvents());

        GUI_MANAGER.startTicking();

        createDefaultTags();

        CONFIG = new ConfigSpec();
        CONFIG.define("resource-pack.autohost", true);
        CONFIG.define("resource-pack.ip", "127.0.0.1");
        CONFIG.define("resource-pack.port", 8080);
        Config.register(MODID, CONFIG);

        if (CONFIG.getBoolean("resource-pack.autohost")) {
            bus.register(new PackEvent());
            PACK_SERVER = new PackServer();
            PACK_SERVER.start(CONFIG.getString("resource-pack.ip"), CONFIG.getInt("resource-pack.port"));
        }

        new ResourcePack(this, MODID).generate();
        // Apply registries
        TestItems.ITEMS.apply();
        ModBlocks.BLOCKS.apply();
        TestRecipes.RECIPES.apply();

        new Glyph(this, new ResourceLocation(MODID, "magic_wand"), 8, 8, true);
        new Glyph(this, new ResourceLocation(MODID, "items_ui_main"), 129, 13, false);
        new Glyph(this, new ResourceLocation(MODID, "items_ui_display"), 129, 13, false);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void createDefaultTags() {
        BuiltinRegistries.ITEM_TAGS.createTag(new ResourceLocation(MODID, "axes"));
        BuiltinRegistries.ITEM_TAGS.createTag(new ResourceLocation(MODID, "pickaxes"));
        BuiltinRegistries.ITEM_TAGS.createTag(new ResourceLocation(MODID, "hoes"));
        BuiltinRegistries.ITEM_TAGS.createTag(new ResourceLocation(MODID, "shovels"));
        BuiltinRegistries.ITEM_TAGS.createTag(new ResourceLocation(MODID, "swords"));
    }

    private boolean checkRPManager() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("ResourcePackManager");
        return plugin != null && plugin.isEnabled();
    }

    public static AbyssalLib getInstance() {
        return INSTANCE;
    }

}
