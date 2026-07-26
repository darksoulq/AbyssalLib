package com.github.darksoulq.abyssallib.world.advancement;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.ops.JsonOps;
import com.github.darksoulq.abyssallib.common.util.Try;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import net.kyori.adventure.key.Key;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementTree;
import net.minecraft.advancements.TreeNodePosition;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Utility class to load Advancements from JSON files
 */
public class AdvancementLoader {

    private static final Path ADVANCEMENTS_FOLDER = new File(AbyssalLib.getInstance().getDataFolder(), "advancements").toPath();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Loads advancements from the libraries advancements folder
     */
    @ApiStatus.Internal
    public static void load() {
        if (!Files.exists(ADVANCEMENTS_FOLDER)) {
            try {
                Files.createDirectories(ADVANCEMENTS_FOLDER);
            } catch (IOException e) {
                AbyssalLib.LOGGER.severe("Failed to create advancements folder: " + e.getMessage());
                return;
            }
        }

        List<Advancement> pendingAdvancements = new ArrayList<>();

        try (Stream<Path> stream = Files.walk(ADVANCEMENTS_FOLDER)) {
            stream.filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".json"))
                .forEach(path -> {
                    Key id = getAdvancementId(path);
                    if (id != null) {
                        Advancement structure = load(path, id);
                        if (structure != null) {
                            if (Registries.ADVANCEMENTS.contains(id.asString())) {
                                AbyssalLib.LOGGER.warning("Duplicate advancement ID '" + id + "' found in file " + path + ". Skipping registration.");
                            } else {
                                Registries.ADVANCEMENTS.register(id.asString(), structure);
                                pendingAdvancements.add(structure);
                            }
                        }
                    }
                });
        } catch (IOException e) {
            AbyssalLib.LOGGER.severe("Failed to walk advancements folder: " + e.getMessage());
        }

        if (!pendingAdvancements.isEmpty()) {
            ServerAdvancementManager manager = MinecraftServer.getServer().getAdvancements();
            Map<Identifier, AdvancementHolder> mutableAdvancements = new HashMap<>(manager.advancements);
            List<AdvancementHolder> newHolders = new ArrayList<>();

            for (Advancement customAdv : pendingAdvancements) {
                AdvancementHolder holder = customAdv.toNMSHolder();
                newHolders.add(holder);
                mutableAdvancements.put(holder.id(), holder);
            }

            manager.advancements = Map.copyOf(mutableAdvancements);
            AdvancementTree tree = manager.tree();
            tree.addAll(newHolders);

            for (AdvancementNode root : tree.roots()) {
                if (root.advancement().display().isPresent()) {
                    TreeNodePosition.run(root);
                }
            }

            for (AdvancementHolder holder : newHolders) {
                Advancement customAdv = Registries.ADVANCEMENTS.get(holder.id().toString());
                if (customAdv != null && customAdv.getDisplay() != null) {
                    float customX = customAdv.getDisplay().getX();
                    float customY = customAdv.getDisplay().getY();
                    if (!Float.isNaN(customX) && !Float.isNaN(customY)) {
                        holder.value().display().ifPresent(info -> info.setLocation(customX, customY));
                    }
                }
            }

            for (Player player : Bukkit.getOnlinePlayers()) {
                ServerPlayer sp = ((CraftPlayer) player).getHandle();
                sp.getAdvancements().reload(manager);
            }
        }
    }

    /**
     * Lads an advancement from a JDON with the provided ID for the instance
     *
     * @param path The target file location.
     * @param id   The ID to set for the advancement.
     * @return The parsed advancement instance.
     */
    public static Advancement load(Path path, Key id) {
        return Try.of(() -> {
            JsonNode root = MAPPER.readTree(path.toFile());
            if (root.isObject()) {
                ((ObjectNode) root).put("id", id.asString());
            }
            DataResult<Advancement> res = Advancement.CODEC.decode(JsonOps.INSTANCE, root);
            if (res.isError()) {
                AbyssalLib.LOGGER.warning("Failed to decode advancement from " + path + ": " + res.error().get());
                return null;
            }
            if (res.isPartial()) {
                res.warnings().forEach(w -> AbyssalLib.LOGGER.warning("Warning decoding advancement " + path + ": " + w.message()));
            }
            return res.getOrThrow();
        }).onFailure(e -> AbyssalLib.LOGGER.warning("Failed to load advancement from " + path + ": " + e.getMessage())).orElse(null);
    }

    /**
     * Loads an advancement from a given file from within the resources/ folder of a plugin with the provided ID
     *
     * @param plugin       The target Plugin.
     * @param resourcePath The target file.
     * @param id           The ID to set for the advancement.
     * @return The parsed advancement instance.
     */
    public static Advancement loadResource(Plugin plugin, String resourcePath, Key id) {
        return Try.of(() -> {
            try (InputStream in = plugin.getResource(resourcePath)) {
                if (in == null) return null;
                JsonNode root = MAPPER.readTree(in);
                if (root.isObject()) {
                    ((ObjectNode) root).put("id", id.asString());
                }
                DataResult<Advancement> res = Advancement.CODEC.decode(JsonOps.INSTANCE, root);
                if (res.isError()) {
                    plugin.getLogger().warning("Failed to decode advancement resource " + resourcePath + ": " + res.error().get());
                    return null;
                }
                if (res.isPartial()) {
                    res.warnings().forEach(w -> plugin.getLogger().warning("Warning decoding advancement resource " + resourcePath + ": " + w.message()));
                }
                return res.getOrThrow();
            }
        }).onFailure(Throwable::printStackTrace).orElse(null);
    }

    /**
     * Saves an advancement into the libraries advancement folder
     *
     * @param id          The Location to store the advancement at (namespace:/path/to/file).
     * @param advancement The advancement to save.
     * @return Whether the save succeeded or not.
     */
    public static boolean save(Key id, Advancement advancement) {
        Path namespaceFolder = ADVANCEMENTS_FOLDER.resolve(id.namespace());
        try {
            if (!Files.exists(namespaceFolder)) Files.createDirectories(namespaceFolder);

            Path file = namespaceFolder.resolve(id.value() + ".json");
            DataResult<JsonNode> res = Advancement.CODEC.encode(JsonOps.INSTANCE, advancement);

            if (res.isError()) {
                AbyssalLib.LOGGER.severe("Failed to encode advancement " + id + ": " + res.error().get());
                return false;
            }
            if (res.isPartial()) {
                res.warnings().forEach(w -> AbyssalLib.LOGGER.warning("Warning encoding advancement " + id + ": " + w.message()));
            }

            MAPPER.writerWithDefaultPrettyPrinter().writeValue(file.toFile(), res.getOrThrow());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gets the ID of an advancement from a file
     *
     * @param file The target file.
     * @return The ID of the advancement.
     */
    private static Key getAdvancementId(Path file) {
        Path relative = ADVANCEMENTS_FOLDER.relativize(file);
        if (relative.getNameCount() < 2) {
            AbyssalLib.LOGGER.warning("Skipping advancement file " + file + ": Must be inside a namespace folder (advancements/<namespace>/<name>.json)");
            return null;
        }

        String namespace = relative.getName(0).toString();
        StringBuilder pathBuilder = new StringBuilder();
        for (int i = 1; i < relative.getNameCount(); i++) {
            if (i > 1) pathBuilder.append("/");
            pathBuilder.append(relative.getName(i));
        }

        String fullPath = pathBuilder.toString();
        int lastDot = fullPath.lastIndexOf('.');
        if (lastDot > 0) {
            fullPath = fullPath.substring(0, lastDot);
        }
        return Key.key(namespace, fullPath);
    }
}