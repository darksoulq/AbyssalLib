package me.darksoul.abyssalLib.resource;

import me.darksoul.abyssalLib.AMod;
import me.darksoul.abyssalLib.AbyssalLib;
import me.darksoul.abyssalLib.util.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public class IPackager implements Listener {

    @EventHandler
    public void onServerReady(ServerLoadEvent event) {
        if (event.getType() == ServerLoadEvent.LoadType.STARTUP) {
            repack();
        }
    }

    public static void repack() {
        AbyssalLib instance = AbyssalLib.getInstance();
        File packFolder = new File(instance.getDataFolder(), "pack");

        FileUtils.createDirectories(packFolder);
        File pack = new File(packFolder, "generated.zip");
        File packGenFolder = new File(packFolder, "resourcepack");
        FileUtils.createDirectories(packGenFolder);

        File assetsFolder = new File(packGenFolder, "assets");
        FileUtils.createDirectories(assetsFolder);

        Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
            AMod.getMods().forEach((mod) -> {
                Plugin plugin = mod.getPlugin();
                List<String> filePaths = mod.getResources();

                for (String filepath : filePaths) {
                    InputStream inputStream = plugin.getResource(filepath);
                    if (inputStream == null) {
                        continue;
                    }
                    File file = new File(assetsFolder, filepath.replaceFirst("^assets", ""));
                    File fileParents = file.getParentFile();
                    FileUtils.createDirectories(fileParents);
                    FileUtils.saveFile(inputStream, file);
                }
            });
            FileUtils.zipFolder(packGenFolder, pack);
        });
    }
}
