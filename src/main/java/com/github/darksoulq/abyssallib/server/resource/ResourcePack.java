package com.github.darksoulq.abyssallib.server.resource;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.util.FileUtils;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.common.util.TextUtil;
import com.github.darksoulq.abyssallib.server.HookConstants;
import com.github.darksoulq.abyssallib.server.event.EventBus;
import com.github.darksoulq.abyssallib.server.event.custom.server.ResourcePackGenerateEvent;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.server.resource.asset.Icon;
import com.github.darksoulq.abyssallib.server.resource.asset.Model;
import com.github.darksoulq.abyssallib.server.resource.asset.PackMcMeta;
import com.github.darksoulq.abyssallib.server.resource.asset.Texture;
import com.github.darksoulq.abyssallib.server.resource.asset.definition.Selector;
import com.github.darksoulq.abyssallib.world.item.Item;
import com.github.darksoulq.abyssallib.world.item.component.builtin.ItemModel;
import com.github.darksoulq.abyssallib.world.item.component.builtin.ItemName;
import com.magmaguy.resourcepackmanager.api.ResourcePackManagerAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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
    private final String pluginId;

    /** Output path: {@code <plugin>/pack/resourcepack.zip} */
    private final Path outputFile;

    /** The icon to use for the pack (can remain null) */
    private Icon icon = null;

    /** The McMeta file of the pack (can be null) */
    private PackMcMeta mcMeta = null;

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
     * @param pluginId     The modid
     */
    public ResourcePack(@NotNull Plugin plugin, @NotNull String pluginId) {
        this.plugin = plugin;
        this.pluginId = pluginId;
        this.outputFile = plugin.getDataFolder().toPath().resolve("pack").resolve("resourcepack.zip");

        uuidMap.put(pluginId, UUID.randomUUID());
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
     * Sets the Icon for the pack.
     *
     * @param data The icon image data
     */
    public void icon(byte[] data) {
        this.icon = new Icon(data);
    }
    /**
     * Autoloads the Icon for this pack from {@code resourcepack/pack.png}
     */
    public void icon() {
        this.icon = new Icon(plugin);
    }

    /**
     * Sets the pack.mcmeta of this resourcepack
     *
     * @param mcmeta The PackMcMeta instance
     */
    public void mcmeta(PackMcMeta mcmeta) {
        this.mcMeta = mcmeta;
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
        if (!override && outputFile.toFile().exists()) {
            hashMap.put(pluginId, FileUtils.sha1(outputFile));
            for (Namespace ns : namespaces.values()) {
                Item icon = new Item(Identifier.of(ns.getNamespace(), "plugin_icon"), Material.GRASS_BLOCK);
                if (ns.getIcon() == null) {
                    icon.setData(new ItemModel(NamespacedKey.fromString("grass")));
                }
                Registries.ITEMS.register(icon.getId().toString(), icon);
            }
            return;
        }
        files.clear();

        for (Namespace ns : namespaces.values()) {
            Item icon = new Item(Identifier.of(ns.getNamespace(), "plugin_icon"), Material.GRASS_BLOCK);
            Texture nsIcon = ns.getIcon();
            if (nsIcon == null) {
                icon.setData(new ItemModel(NamespacedKey.fromString("grass")));
            } else {
                nsIcon.emit(files);
                Model model = ns.model("plugin_icon", false);
                model.parent("builtin/generated");
                model.texture("layer0", nsIcon);
                Selector.Model sel = new Selector.Model(model);
                ns.itemDefinition("plugin_icon", sel);
            }
            Registries.ITEMS.register(icon.getId().toString(), icon);
            ns.emit(files);
        }

        if (mcMeta != null) {
            mcMeta.emit(files);
        } else {
            put("pack.mcmeta", generatePackMeta());
        }
        if (icon != null) icon.emit(files);

        try {
            Files.createDirectories(outputFile.getParent());
            try (ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(outputFile))) {
                for (Map.Entry<String, byte[]> entry : files.entrySet()) {
                    zip.putNextEntry(new ZipEntry(entry.getKey()));
                    zip.write(entry.getValue());
                    zip.closeEntry();
                }
            }

            hashMap.put(pluginId, FileUtils.sha1(outputFile));
            EventBus.post(new ResourcePackGenerateEvent(pluginId, outputFile.toFile()));

        } catch (IOException e) {
            throw new RuntimeException("Failed to write resource pack: " + outputFile, e);
        }
    }

    /**
     * Compiles and registers the pack for sending to players.
     * @param override whether to generate zip if it has been generated before.
     */
    public void register(boolean override) {
        compile(override);
        if (AbyssalLib.PACK_SERVER != null) {
            AbyssalLib.PACK_SERVER.registerResourcePack(pluginId, outputFile);
        } else if (HookConstants.isEnabled(HookConstants.Plugin.RSPM)) {
            ResourcePackManagerAPI.registerLocalResourcePack(
                    plugin.getName(),
                    plugin.getName() + "/pack/resourcepack.zip",
                    false,
                    true,
                    true,
                    null
            );
        }
    }

    public void unregister() {
        if (AbyssalLib.PACK_SERVER != null) {
            AbyssalLib.PACK_SERVER.unregisterResourcePack(pluginId);
        }
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
                "    \"description\": \"" + pluginId + " internal Resource Pack\"\n" +
                "  }\n" +
                "}";
    }
}
