package me.darksoul.abyssalLib.resource;

import com.magmaguy.resourcepackmanager.api.ResourcePackManagerAPI;
import me.darksoul.abyssalLib.AbyssalLib;
import me.darksoul.abyssalLib.util.FileUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ResourcePack {

    private final JavaPlugin plugin;
    private final String modID;
    private final Path outputZip;
    public static final Map<String, String> hashMap = new HashMap<>();
    public static final Map<String, UUID> uuidMap = new HashMap<>();

    public ResourcePack(JavaPlugin plugin, String modID) {
        this.plugin = plugin;
        this.modID =modID;
        this.outputZip = plugin.getDataFolder().toPath().resolve("pack/resourcepack.zip");
    }

    public void generate() {
        try {
            if (!outputZip.toFile().exists()) {
                Files.createDirectories(outputZip.getParent());
                File packGenFolder = new File(outputZip.getParent().toFile(), "resourcepack");
                FileUtils.createDirectories(packGenFolder);
                File mcMetaF = new File(packGenFolder, "pack.mcmeta");
                File assetsFolder = new File(packGenFolder, "assets");
                FileUtils.createDirectories(assetsFolder);

                List<String> filePaths = FileUtils.getFilePathList(plugin, "assets/");

                InputStream mcMeta = plugin.getResource("pack.mcmeta");

                if (mcMeta == null) {
                    AbyssalLib.getInstance().getLogger().severe("pack.mcmeta not found for: " + plugin.getName());
                    return;
                }

                FileUtils.saveFile(mcMeta, mcMetaF);
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
                FileUtils.zipFolder(packGenFolder, outputZip.toFile());
                FileUtils.deleteFolder(packGenFolder.toPath());
                plugin.getLogger().info("Generated resource pack at: " + outputZip.toAbsolutePath());
            }

            if (AbyssalLib.CONFIG.getBoolean("resource-pack.autohost")) {
                hashMap.put(modID, FileUtils.sha1(outputZip));
                uuidMap.put(modID, UUID.randomUUID());
                AbyssalLib.PACK_SERVER.registerResourcePack(modID, outputZip);
            } else if (!AbyssalLib.CONFIG.getBoolean("resource-pack.autohost") && AbyssalLib.isRPManagerInstalled) {
                ResourcePackManagerAPI.registerResourcePack(
                        plugin.getName(),
                        plugin.getName() + "/pack/resourcepack.zip",
                        false,
                        true,
                        true,
                        true,
                        null
                );
            }

        } catch (Exception e) {
            plugin.getLogger().severe("Failed to generate resource pack: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
