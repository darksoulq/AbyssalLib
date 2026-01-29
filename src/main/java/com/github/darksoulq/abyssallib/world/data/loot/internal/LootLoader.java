package com.github.darksoulq.abyssallib.world.data.loot.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.serialization.ops.JsonOps;
import com.github.darksoulq.abyssallib.common.util.FileUtils;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.data.loot.LootTable;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class LootLoader {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final File FOLDER = new File(AbyssalLib.getInstance().getDataFolder(), "loot_tables");

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

    public static LootTable load(Path path) {
        try {
            JsonNode root = MAPPER.readTree(path.toFile());
            return LootTable.CODEC.decode(JsonOps.INSTANCE, root);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

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