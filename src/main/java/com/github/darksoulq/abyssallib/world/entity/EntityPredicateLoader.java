package com.github.darksoulq.abyssallib.world.entity;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.serialization.ops.YamlOps;
import com.github.darksoulq.abyssallib.common.util.Try;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import net.kyori.adventure.key.Key;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Utility class responsible for loading and parsing data-driven {@link EntityPredicate} definitions.
 * This loader scans the server's data folder for YAML files, deserializes them using
 * the configured codec, and injects them into the global entity predicate registry.
 */
public class EntityPredicateLoader {

    /**
     * The root directory where custom entity predicate YAML files are stored.
     */
    private static final Path PREDICATES_FOLDER = new java.io.File(AbyssalLib.getInstance().getDataFolder(), "predicates/entity").toPath();

    /**
     * Scans the predicates directory and loads all valid YAML configurations into the registry.
     * If the base directory does not exist, it will be created automatically.
     */
    public static void loadPredicates() {
        if (!Files.exists(PREDICATES_FOLDER)) {
            try {
                Files.createDirectories(PREDICATES_FOLDER);
            } catch (IOException e) {
                AbyssalLib.LOGGER.severe("Failed to create entity predicates folder: " + e.getMessage());
                return;
            }
        }

        try (Stream<Path> stream = Files.walk(PREDICATES_FOLDER)) {
            stream.filter(Files::isRegularFile)
                .filter(path -> {
                    String name = path.getFileName().toString().toLowerCase();
                    return name.endsWith(".yml") || name.endsWith(".yaml");
                })
                .forEach(EntityPredicateLoader::loadSingle);
        } catch (IOException e) {
            AbyssalLib.LOGGER.severe("Failed to walk entity predicates folder: " + e.getMessage());
        }
    }

    /**
     * Processes a single file path, attempting to parse and register the entity predicate.
     *
     * @param path
     * The {@link Path} to the YAML file.
     */
    private static void loadSingle(Path path) {
        Key id = getPredicateId(path);
        if (id == null) {
            return;
        }

        EntityPredicate predicate = load(path);
        if (predicate != null) {
            Registries.ENTITY_PREDICATES.register(id.asString(), predicate);
        }
    }

    /**
     * Reads a YAML file from the file system and decodes it into an EntityPredicate instance.
     *
     * @param path
     * The {@link Path} to the YAML file to read.
     * @return
     * The parsed {@link EntityPredicate}, or null if parsing fails.
     */
    public static EntityPredicate load(Path path) {
        return Try.of(() -> {
            try (InputStream in = Files.newInputStream(path)) {
                Object root = YamlOps.INSTANCE.parse(in);
                return EntityPredicate.CODEC.decode(YamlOps.INSTANCE, root);
            }
        }).onFailure(e -> e.printStackTrace()).orElse(null);
    }

    /**
     * Reads a YAML file bundled within a plugin's JAR resources and decodes it.
     *
     * @param plugin
     * The {@link Plugin} instance owning the resource.
     * @param resourcePath
     * The internal path to the YAML resource file.
     * @return
     * The parsed {@link EntityPredicate}, or null if the resource is missing or parsing fails.
     */
    public static EntityPredicate loadResource(Plugin plugin, String resourcePath) {
        return Try.of(() -> {
            try (InputStream in = plugin.getResource(resourcePath)) {
                if (in == null) {
                    return null;
                }
                Object root = YamlOps.INSTANCE.parse(in);
                return EntityPredicate.CODEC.decode(YamlOps.INSTANCE, root);
            }
        }).onFailure(e -> e.printStackTrace()).orElse(null);
    }

    /**
     * Determines the unique Key for a predicate based on its directory structure.
     * The structure must be {@code predicates/entity/<namespace>/<path_to_file>.yml}.
     *
     * @param file
     * The {@link Path} of the file being evaluated.
     * @return
     * The resolved {@link Key}, or null if the directory structure is invalid.
     */
    private static Key getPredicateId(Path file) {
        Path relative = PREDICATES_FOLDER.relativize(file);
        if (relative.getNameCount() < 2) {
            AbyssalLib.LOGGER.warning("Skipping file " + file + ": Must be inside a namespace folder (predicates/entity/<namespace>/<name>.yml)");
            return null;
        }

        String namespace = relative.getName(0).toString();
        StringBuilder pathBuilder = new StringBuilder();
        for (int i = 1; i < relative.getNameCount(); i++) {
            if (i > 1) {
                pathBuilder.append("/");
            }
            pathBuilder.append(relative.getName(i).toString());
        }

        String fullPath = pathBuilder.toString();
        int lastDot = fullPath.lastIndexOf('.');
        if (lastDot > 0) {
            fullPath = fullPath.substring(0, lastDot);
        }
        return Key.key(namespace, fullPath);
    }
}