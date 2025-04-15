package me.darksoul.abyssalLib;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.darksoul.abyssalLib.block.BlockManager;
import me.darksoul.abyssalLib.block.test.ModBlocks;
import me.darksoul.abyssalLib.block.test.TestBlock;
import me.darksoul.abyssalLib.command.CommandBus;
import me.darksoul.abyssalLib.config.Config;
import me.darksoul.abyssalLib.config.ConfigSpec;
import me.darksoul.abyssalLib.event.*;
import me.darksoul.abyssalLib.gui.GuiManager;
import me.darksoul.abyssalLib.item.test.PlayerEvents;
import me.darksoul.abyssalLib.item.test.TestItems;
import me.darksoul.abyssalLib.recipe.test.TestRecipes;
import me.darksoul.abyssalLib.registry.BuiltinRegistries;
import me.darksoul.abyssalLib.resource.PackServer;
import me.darksoul.abyssalLib.resource.ResourcePack;
import me.darksoul.abyssalLib.resource.glyph.Glyph;
import me.darksoul.abyssalLib.resource.glyph.GlyphManager;
import me.darksoul.abyssalLib.util.ResourceLocation;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

public final class AbyssalLib extends JavaPlugin {
    public static String MODID = "abyssallib";
    private static AbyssalLib INSTANCE;
    public static PackServer PACK_SERVER;
    private static File CONFIG_FILE;
    public static YamlConfiguration CONFIG;
    public static GuiManager GUI_MANAGER;

    public static boolean isRPManagerInstalled = false;

    @Override
    public void onEnable() {
        INSTANCE = this;
        isRPManagerInstalled = checkRPManager();

        BlockManager.INSTANCE.load();
        GUI_MANAGER = new GuiManager();
        EventBus bus = new EventBus(this);

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            CommandBus.init(commands.registrar().getDispatcher());
        });

        bus.register(GUI_MANAGER);
        bus.register(new PlayerEvents());
        bus.register(new BlockEvents());
        bus.register(new ItemEvents());
        bus.register(new ServerEvents());

        GUI_MANAGER.startTicking();

        createDefaultTags();

        CONFIG_FILE = new File(getInstance().getDataFolder(), "config.yml");
        copyTemplate("config.yml", CONFIG_FILE);
        CONFIG = loadConfig();

        if (CONFIG.getBoolean("resource-pack.autohost")) {
            bus.register(new PackEvent());
            PACK_SERVER = new PackServer();
            PACK_SERVER.start(CONFIG.getString("resource-pack.ip"), CONFIG.getInt("resource-pack.port"));
        }
        // test
        // Apply registries
        TestItems.ITEMS.apply();
        ModBlocks.BLOCKS.apply();
        TestRecipes.RECIPES.apply();

        // Glyph test
        GlyphManager.register(this, new Glyph(new ResourceLocation(MODID, "magic_wand"), 8, 8, true));

        // Config test
        ConfigSpec spec = new ConfigSpec();
        spec.define("test.int", 5);
        spec.define("test.string.string", "test");
        spec.define("test.list", List.of(1, 2, 3, 4));
        Config.register(MODID, "server", spec);

        getLogger().info("int: " + spec.getInt("test.int") + " string: " + spec.getString("test.string.string") + " list: " + spec.getList("test.list", Integer.class));

        new ResourcePack(this, MODID).generate();
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

    private static void copyTemplate(String resourceName, File destination) {
        if (!destination.exists()) {
            try (InputStream resourceStream = AbyssalLib.getInstance().getResource(resourceName)) {
                if (resourceStream != null) {
                    Files.copy(resourceStream, destination.toPath());
                } else {
                    destination.createNewFile();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static YamlConfiguration loadConfig() {
        return YamlConfiguration.loadConfiguration(CONFIG_FILE);
    }

}
