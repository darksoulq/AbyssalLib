package me.darksoul.abyssalLib;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.darksoul.abyssalLib.command.ICommands;
import me.darksoul.abyssalLib.resource.IHoster;
import me.darksoul.abyssalLib.resource.IPackager;
import me.darksoul.abyssalLib.util.FileUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class AbyssalLib extends JavaPlugin {
    private static AbyssalLib instance;

    public static AbyssalLib getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        getServer().getPluginManager().registerEvents(new IPackager(), instance);
        saveDefaultConfig();
        try {
            createDefaultFiles();
            getConfig().load(new File(getDataFolder(), "config.yml"));
            if (getConfig().getBoolean("host.autohost", false)) {
                IHoster server = new IHoster();
                getServer().getPluginManager().registerEvents(server, instance);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        new ExampleMod();

        // Command
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(ICommands.createCommand().build());
        });
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void createDefaultFiles() throws IOException {
        File packmcmeta = new File(getDataFolder(), "pack/resourcepack/pack.mcmeta");
        File packpng = new File(getDataFolder(), "pack/resourcepack/pack.png");
        File rpFolder = packmcmeta.getParentFile();
        FileUtils.createDirectories(rpFolder);
        if (!packmcmeta.exists()) {
            packmcmeta.createNewFile();
            FileUtils.saveFile(getResource("pack.mcmeta"), packmcmeta);
        }
        if (!packpng.exists()) {
            FileUtils.saveFile(getResource("pack.png"), packpng);
        }
    }
}
