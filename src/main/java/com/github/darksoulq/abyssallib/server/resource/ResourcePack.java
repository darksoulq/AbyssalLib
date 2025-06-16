package com.github.darksoulq.abyssallib.server.resource;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.event.custom.server.ResourcePackGenerateEvent;
import com.github.darksoulq.abyssallib.util.FileUtils;
import com.magmaguy.resourcepackmanager.api.ResourcePackManagerAPI;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Handles creation, namespacing, and compilation of a resource pack.
 * Output is written to a ZIP at {@code pluginFolder/pack/output.zip}.
 */
public class ResourcePack {

    /** Plugin that owns this pack. */
    private final Plugin plugin;

    /** The modid. */
    private final String modid;

    /** Output path: {@code <plugin>/pack/resourcepack.zip} */
    private final Path outputFile;

    /** Files to write into the zip. */
    private final Map<String, byte[]> files = new TreeMap<>();

    /** Namespace containers for assets. */
    private final Map<String, Namespace> namespaces = new HashMap<>();

    /** Tracks UUIDs for registered packs (used by the sending server). */
    public static final Map<String, UUID> uuidMap = new HashMap<>();

    /** Tracks SHA1 hashes of compiled packs. */
    public static final Map<String, String> hashMap = new HashMap<>();

    /**
     * Creates a new resource pack instance.
     *
     * @param plugin Owning plugin
     * @param modid     The modid
     */
    public ResourcePack(@NotNull Plugin plugin, @NotNull String modid) {
        this.plugin = plugin;
        this.modid = modid;
        this.outputFile = plugin.getDataFolder().toPath().resolve("pack").resolve("resourcepack.zip");

        uuidMap.put(modid, UUID.randomUUID());
    }

    /**
     * Returns or creates a namespace container.
     *
     * @param namespace Domain (e.g. {@code myplugin})
     * @return Namespace container
     */
    public @NotNull Namespace namespace(@NotNull String namespace) {
        return namespaces.computeIfAbsent(namespace, ns -> new Namespace(ns, this));
    }

    /**
     * @return All namespaces in this pack
     */
    public @NotNull Collection<Namespace> getNamespaces() {
        return namespaces.values();
    }

    /**
     * @return Final path to the compiled ZIP
     */
    public @NotNull Path getOutputFile() {
        return outputFile;
    }

    /**
     * @return Owning plugin
     */
    public @NotNull Plugin getPlugin() {
        return plugin;
    }

    /**
     * Compiles and zips the pack contents asynchronously.
     * @param override whether to generate zip if it has been generated before
     */
    public void compile(boolean override) {
        if (!override && outputFile.toFile().exists()) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                files.clear();

                for (Namespace ns : namespaces.values()) {
                    ns.emit(files);
                }

                put("pack.mcmeta", generatePackMeta());

                try {
                    Files.createDirectories(outputFile.getParent());
                    try (ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(outputFile))) {
                        for (Map.Entry<String, byte[]> entry : files.entrySet()) {
                            zip.putNextEntry(new ZipEntry(entry.getKey()));
                            zip.write(entry.getValue());
                            zip.closeEntry();
                        }
                    }

                    hashMap.put(modid, FileUtils.sha1(outputFile));
                    AbyssalLib.EVENT_BUS.post(new ResourcePackGenerateEvent(modid, outputFile.toFile()));

                } catch (IOException e) {
                    throw new RuntimeException("Failed to write resource pack: " + outputFile, e);
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    /**
     * Compiles and registers the pack for sending to players.
     * @param override whether to generate zip if it has been generated before.
     */
    public void register(boolean override) {
        if (!override && outputFile.toFile().exists()) return;
        compile(override);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (AbyssalLib.PACK_SERVER != null) {
                    AbyssalLib.PACK_SERVER.registerResourcePack(modid, outputFile);
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
            }
        }.runTaskLaterAsynchronously(plugin, 20);
    }

    /**
     * Adds a raw binary file to the pack.
     *
     * @param path Pack-relative path
     * @param data File data
     */
    protected void put(@NotNull String path, byte @NotNull [] data) {
        files.put(path, data);
    }

    /**
     * Adds a UTF-8 text file to the pack.
     *
     * @param path Pack-relative path
     * @param text File content
     */
    protected void put(@NotNull String path, @NotNull String text) {
        files.put(path, text.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Builds the default {@code pack.mcmeta} file as a string.
     *
     * @return JSON content of pack.mcmeta
     */
    private @NotNull String generatePackMeta() {
        return "{\n" +
                "  \"pack\": {\n" +
                "    \"pack_format\": 55,\n" +
                "    \"description\": \"" + modid + " internal Resource Pack\"\n" +
                "  }\n" +
                "}";
    }
}
