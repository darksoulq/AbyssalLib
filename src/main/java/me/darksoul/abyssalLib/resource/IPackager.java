package me.darksoul.abyssalLib.resource;

import me.darksoul.abyssalLib.AbyssalLib;
import me.darksoul.abyssalLib.util.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.util.List;
import java.util.Map;

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
            for (Map.Entry<String, List<String>> entry : AResource.getResources().entrySet()) {
                Plugin plugin = AResource.getMods().get(entry.getKey());
                List<String> filePaths = entry.getValue();
                File namespaceFolder = new File(assetsFolder, entry.getKey());

                if (plugin == null || !plugin.isEnabled()) {
                    continue;
                }
                FileUtils.createDirectories(namespaceFolder);

                for (String filepath : filePaths) {
                    InputStream inputStream = plugin.getResource(filepath);
                    if (inputStream == null) {
                        continue;
                    }
                    File file = new File(namespaceFolder, filepath);
                    File fileParents = file.getParentFile();
                    FileUtils.createDirectories(fileParents);
                    FileUtils.saveFile(inputStream, file);
                }
            }
            FileUtils.zipFolder(packGenFolder, pack);
        });
    }
}
