package com.github.darksoulq.abyssallib.world.data.loot.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.serialization.ops.JsonOps;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.data.loot.LootTable;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Internal utility class responsible for loading and registering {@link LootTable}s.
 * <p>
 * This loader scans the {@code loot_tables/} directory in the plugin data folder,
 * parsing {@code .json} files into loot table objects and registering them with
 * their namespaced identifiers.
 * </p>
 */
public class LootLoader {
    /** The Jackson {@link ObjectMapper} used for parsing JSON data into tree structures. */
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /** The directory path where external loot table files are stored. */
    private static final File FOLDER = new File(AbyssalLib.getInstance().getDataFolder(), "loot_tables");

    /**
     * Orchestrates the discovery and loading of loot tables from the local file system.
     * <p>
     * If the {@code loot_tables/} directory does not exist, it will be created.
     * Otherwise, it performs a recursive walk through the folder to find and
     * process all valid {@code .json} files.
     * </p>
     */
    public static void load() {
        if (!FOLDER.exists()) {
            FOLDER.mkdirs();
            return;
        }

        try (Stream<Path> paths = Files.walk(FOLDER.toPath())) {
            paths.filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".json"))
                .forEach(LootLoader::loadFileAndRegister);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Processes an individual file path, determines its namespaced ID, and registers the resulting table.
     * <p>
     * The ID is derived from the directory structure relative to the {@code loot_tables/} folder.
     * For example: {@code loot_tables/my_namespace/sub/table.json} results in {@code "my_namespace:sub/table"}.
     * </p>
     *
     * @param path The {@link Path} to the JSON file to be loaded.
     */
    private static void loadFileAndRegister(Path path) {
        try {
            Path relative = FOLDER.toPath().relativize(path);
            String namespace = relative.getName(0).toString();
            String key = relative.subpath(1, relative.getNameCount()).toString().replace(File.separator, "/").replace(".json", "");
            String id = namespace + ":" + key;

            LootTable table = load(path);
            if (table != null) {
                Registries.LOOT_TABLES.register(id, table);
            }
        } catch (Exception e) {
            AbyssalLib.LOGGER.warning("Failed to load loot table " + path);
            e.printStackTrace();
        }
    }

    /**
     * Decodes a {@link LootTable} from a file on the disk.
     *
     * @param path The {@link Path} of the file to read.
     * @return The decoded {@link LootTable}, or {@code null} if parsing fails.
     */
    public static LootTable load(Path path) {
        try {
            JsonNode root = MAPPER.readTree(path.toFile());
            return LootTable.CODEC.decode(JsonOps.INSTANCE, root);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Decodes a {@link LootTable} from a plugin's embedded resource.
     *
     * @param plugin       The {@link Plugin} instance containing the resource.
     * @param resourcePath The internal path to the resource file (e.g., "loot/default.json").
     * @return The decoded {@link LootTable}, or {@code null} if the resource is missing or invalid.
     */
    public static LootTable loadResource(Plugin plugin, String resourcePath) {
        try (InputStream in = plugin.getResource(resourcePath)) {
            if (in == null) return null;
            JsonNode root = MAPPER.readTree(in);
            return LootTable.CODEC.decode(JsonOps.INSTANCE, root);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}