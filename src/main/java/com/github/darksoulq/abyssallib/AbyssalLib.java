package com.github.darksoulq.abyssallib;

import com.github.darksoulq.abyssallib.block.BlockManager;
import com.github.darksoulq.abyssallib.command.CommandBus;
import com.github.darksoulq.abyssallib.config.Config;
import com.github.darksoulq.abyssallib.config.ConfigSpec;
import com.github.darksoulq.abyssallib.event.EventBus;
import com.github.darksoulq.abyssallib.event.internal.*;
import com.github.darksoulq.abyssallib.gui.GuiManager;
import com.github.darksoulq.abyssallib.item.Items;
import com.github.darksoulq.abyssallib.recipe.RecipeLoader;
import com.github.darksoulq.abyssallib.recipe.impl.*;
import com.github.darksoulq.abyssallib.registry.BuiltinRegistries;
import com.github.darksoulq.abyssallib.resource.PackServer;
import com.github.darksoulq.abyssallib.resource.ResourcePack;
import com.github.darksoulq.abyssallib.resource.glyph.Glyph;
import com.github.darksoulq.abyssallib.util.ChatInputHandler;
import com.github.darksoulq.abyssallib.util.Metrics;
import com.github.darksoulq.abyssallib.util.ResourceLocation;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
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

        // -- METRICS --
        int pluginId = 25772;
        Metrics metrics = new Metrics(INSTANCE, pluginId);
        // -- END --

        BlockManager.INSTANCE.load();
        GUI_MANAGER = new GuiManager();
        CHAT_INPUT_HANDLER = new ChatInputHandler();
        EventBus bus = new EventBus(this);
        RecipeLoader.init();

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            CommandBus.init(commands.registrar().getDispatcher());
        });

        bus.register(CHAT_INPUT_HANDLER);
        bus.register(new PlayerEvents());
        bus.register(new BlockEvents());
        bus.register(new ItemEvents());
        bus.register(new ServerEvents());
        bus.register(new GuiEvents());

        createDefaultTags();
        registerRecipeDeserializers();
        RecipeLoader.loadAll();

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
        Items.ITEMS.apply();

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
    private void registerRecipeDeserializers() {
        CampfireRecipeImpl.init();
        ShapedRecipeImpl.init();
        ShapelessRecipeImpl.init();
        SmeltingRecipeImpl.init();
        SmithingRecipeImpl.init();
        StonecuttingRecipeImpl.init();
    }

    private boolean checkRPManager() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("ResourcePackManager");
        return plugin != null && plugin.isEnabled();
    }

    public static AbyssalLib getInstance() {
        return INSTANCE;
    }

}
