package com.github.darksoulq.abyssallib.server.resource;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.event.SubscribeEvent;
import com.github.darksoulq.abyssallib.server.event.custom.server.ResourcePackDeleteEvent;
import com.github.darksoulq.abyssallib.server.event.custom.server.ResourcePackGenerateEvent;
import com.github.darksoulq.abyssallib.util.FileUtils;
import com.google.gson.*;
import com.magmaguy.resourcepackmanager.api.ResourcePackManagerAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
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
    private final File packGenFolder;
    public static final Map<String, String> hashMap = new HashMap<>();
    public static final Map<String, UUID> uuidMap = new HashMap<>();

    public ResourcePack(JavaPlugin plugin, String modID) {
        this.plugin = plugin;
        this.modID = modID;
        this.outputZip = plugin.getDataFolder().toPath().resolve("pack/resourcepack.zip");
        this.packGenFolder = new File(outputZip.getParent().toFile(), "resourcepack");

        AbyssalLib.EVENT_BUS.register(this);
    }

    public void generate() {
        try {
            if (!outputZip.toFile().exists()) {
                Files.createDirectories(outputZip.getParent());
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
                    if (filepath.equals("assets/minecraft/font/default.json") && file.exists()) {
                        try (
                                InputStream pluginFontStream = inputStream;
                                Reader pluginReader = new InputStreamReader(pluginFontStream);
                                Reader existingReader = new FileReader(file)
                        ) {
                            JsonObject pluginJson = JsonParser.parseReader(pluginReader).getAsJsonObject();
                            JsonObject existingJson = JsonParser.parseReader(existingReader).getAsJsonObject();

                            JsonArray existingProviders = existingJson.has("providers")
                                    ? existingJson.getAsJsonArray("providers") : new JsonArray();
                            JsonArray pluginProviders = pluginJson.has("providers")
                                    ? pluginJson.getAsJsonArray("providers") : new JsonArray();

                            for (JsonElement element : pluginProviders) {
                                existingProviders.add(element);
                            }

                            existingJson.add("providers", existingProviders);

                            try (Writer writer = new FileWriter(file)) {
                                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                                gson.toJson(existingJson, writer);
                            }

                        } catch (IOException e) {
                            plugin.getLogger().severe("Failed to merge default.json: " + e.getMessage());
                            e.printStackTrace();
                        }
                    } else {
                        FileUtils.saveFile(inputStream, file);
                    }
                }

                Bukkit.getScheduler().runTaskLater(AbyssalLib.getInstance(), () -> {
                    FileUtils.zipFolder(packGenFolder, outputZip.toFile());
                    plugin.getLogger().info("Generated resource pack at: " + outputZip.toAbsolutePath());

                    ResourcePackGenerateEvent generateEvent = new ResourcePackGenerateEvent(modID, packGenFolder, outputZip.toFile());
                    AbyssalLib.EVENT_BUS.post(generateEvent);

                }, 20 * 2);
            }

            Bukkit.getScheduler().runTaskLaterAsynchronously(AbyssalLib.getInstance(), () -> {
                if (AbyssalLib.CONFIG.getBoolean("resource-pack.autohost")) {
                    hashMap.put(modID, FileUtils.sha1(outputZip));
                    uuidMap.put(modID, UUID.randomUUID());
                    AbyssalLib.PACK_SERVER.registerResourcePack(modID, outputZip);
                } else if (AbyssalLib.isRPManagerInstalled) {
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
            }, 20 * 3);

        } catch (Exception e) {
            plugin.getLogger().severe("Failed to generate resource pack: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Listener for resource pack generation to handle deletion.
     */
    @SubscribeEvent
    public void onGenerate(ResourcePackGenerateEvent event) {
        if (!event.modid().equals(this.modID)) return;

        ResourcePackDeleteEvent deleteEvent = new ResourcePackDeleteEvent(modID, packGenFolder, ResourcePackDeleteEvent.Cause.GENERATE);
        AbyssalLib.EVENT_BUS.post(deleteEvent);

        if (deleteEvent.isCancelled()) {
            plugin.getLogger().info("Resource pack deletion cancelled for modid: " + modID);
            return;
        }

        try {
            FileUtils.deleteFolder(packGenFolder.toPath());
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to delete resource pack folder: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
