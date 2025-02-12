package me.darksoul.abyssalLib;

import me.darksoul.abyssalLib.resource.AResource;
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
//        AMod.register("abyssallib", instance);
        getServer().getPluginManager().registerEvents(new IPackager(), instance);
        try {
            createDefaultFiles();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        AResource.loadResources(instance);
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
