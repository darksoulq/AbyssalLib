package com.github.darksoulq.abyssallib.world.data.tag;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.serialization.ops.YamlOps;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Utility class responsible for the discovery, registration, and parsing of tag files.
 * <p>
 * The loader follows a two-pass system:
 * 1. Scan the file system to register tag IDs in the registry.
 * 2. Parse the contents of those files to populate values and handle tag inclusions.
 * </p>
 */
public class TagLoader {
    /** The base directory where tag files are stored. */
    private static final Path TAGS_FOLDER = new java.io.File(AbyssalLib.getInstance().getDataFolder(), "tags").toPath();

    /** The list of registered {@link TagType}s available for loading. */
    private static final List<TagType<?, ?>> TAG_TYPES = new ArrayList<>();

    /**
     * Orchestrates the loading of all tags from the file system.
     * <p>
     * Creates the tag directory if missing, registers found IDs, and then
     * resolves all values and references.
     * </p>
     */
    public static void loadTags() {
        if (!Files.exists(TAGS_FOLDER)) {
            try { Files.createDirectories(TAGS_FOLDER); }
            catch (IOException e) { e.printStackTrace(); }
        }

        for (TagType<?, ?> type : TAG_TYPES) {
            Path typeFolder = TAGS_FOLDER.resolve(type.folder());
            if (!Files.exists(typeFolder)) continue;
            scanAndRegister(typeFolder, type);
        }
        for (TagType<?, ?> type : TAG_TYPES) {
            Path typeFolder = TAGS_FOLDER.resolve(type.folder());
            if (!Files.exists(typeFolder)) continue;
            parseValues(typeFolder, type);
        }
    }

    /**
     * Performs the first pass: identifying tag files and creating registry entries.
     *
     * @param <T>    Entry type.
     * @param <D>    Data type.
     * @param folder The directory for this tag type.
     * @param type   The {@link TagType} being scanned.
     */
    private static <T, D> void scanAndRegister(Path folder, TagType<T, D> type) {
        try (Stream<Path> stream = Files.walk(folder)) {
            stream.filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".yml") || p.toString().endsWith(".yaml"))
                .forEach(file -> {
                    Identifier tagId = getTagId(file, folder);
                    if (tagId != null && !Registries.TAGS.contains(tagId.toString())) {
                        Tag<T, D> tag = type.factory().apply(tagId);
                        Registries.TAGS.register(tagId.toString(), tag);
                    }
                });
        } catch (IOException e) {
            AbyssalLib.LOGGER.warning("Failed to scan tag folder " + folder + ": " + e.getMessage());
        }
    }

    /**
     * Performs the second pass: reading file contents and resolving references.
     *
     * @param <T>    Entry type.
     * @param <D>    Data type.
     * @param folder The directory for this tag type.
     * @param type   The {@link TagType} being parsed.
     */
    @SuppressWarnings("unchecked")
    private static <T, D> void parseValues(Path folder, TagType<T, D> type) {
        try (Stream<Path> stream = Files.walk(folder)) {
            stream.filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".yml") || p.toString().endsWith(".yaml"))
                .forEach(file -> {
                    Identifier tagId = getTagId(file, folder);
                    if (tagId == null) return;

                    Tag<T, D> tag = (Tag<T, D>) Registries.TAGS.get(tagId.toString());
                    if (tag == null) return;

                    List<Object> rawList;
                    try (InputStream in = Files.newInputStream(file)) {
                        rawList = loadRawList(in);
                    } catch (Exception e) {
                        AbyssalLib.LOGGER.warning("Failed to parse tag file " + file + ": " + e.getMessage());
                        return;
                    }
                    if (rawList == null) return;

                    processTagEntries(tag, rawList, type, tagId);
                });
        } catch (IOException e) {
            AbyssalLib.LOGGER.warning("Failed to parse tag values in " + folder + ": " + e.getMessage());
        }
    }

    /**
     * Loads a tag from a plugin's internal resource folder.
     *
     * @param <T>          Entry type.
     * @param <D>          Data type.
     * @param plugin       The {@link Plugin} instance.
     * @param resourcePath Path to the resource file.
     * @param type         The associated {@link TagType}.
     * @param id           The {@link Identifier} to assign to the tag.
     * @return The loaded {@link Tag} instance, or {@code null} if loading failed.
     */
    public static <T, D> Tag<T, D> loadResource(Plugin plugin, String resourcePath, TagType<T, D> type, Identifier id) {
        try (InputStream in = plugin.getResource(resourcePath)) {
            if (in == null) return null;

            Tag<T, D> tag = type.factory().apply(id);
            List<Object> rawList = loadRawList(in);
            if (rawList != null) {
                processTagEntries(tag, rawList, type, id);
            }
            return tag;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Extracts the "values" list from a YAML input stream.
     *
     * @param in The {@link InputStream} to parse.
     * @return A {@link List} of raw objects, or {@code null} if the format is invalid.
     */
    @SuppressWarnings("unchecked")
    private static List<Object> loadRawList(InputStream in) {
        Object root = YamlOps.INSTANCE.parse(in);
        if (!(root instanceof Map<?, ?> map)) return null;
        Object v = map.get("values");
        if (!(v instanceof List<?> list)) return null;
        return (List<Object>) list;
    }

    /**
     * Processes raw objects into tag entries, resolving string references starting with '#'.
     *
     * @param <T>     Entry type.
     * @param <D>     Data type.
     * @param tag     The {@link Tag} to populate.
     * @param rawList The list of raw entries.
     * @param type    The {@link TagType} definition.
     * @param tagId   The ID of the tag being processed for logging.
     */
    @SuppressWarnings("unchecked")
    private static <T, D> void processTagEntries(Tag<T, D> tag, List<Object> rawList, TagType<T, D> type, Identifier tagId) {
        for (Object rawEntry : rawList) {
            if (rawEntry instanceof String s && s.startsWith("#")) {
                String refId = s.substring(1);
                Tag<?, ?> rawIncluded = Registries.TAGS.get(refId);

                if (rawIncluded == null) {
                    AbyssalLib.LOGGER.warning("Tag " + tagId + " references missing tag: " + refId);
                    continue;
                }

                try {
                    tag.include((Tag<T, D>) rawIncluded);
                } catch (ClassCastException ex) {
                    AbyssalLib.LOGGER.warning("Tag " + tagId + " tried to include incompatible tag type " + refId);
                }
                continue;
            }
            try {
                T value = type.codec().decode(YamlOps.INSTANCE, rawEntry);
                tag.add(value);
            } catch (Exception e) {
                AbyssalLib.LOGGER.warning("Error decoding value in tag " + tagId + ": " + e.getMessage());
            }
        }
    }

    /**
     * Resolves a file path into a namespaced {@link Identifier}.
     *
     * @param file       The path to the YAML file.
     * @param rootFolder The root folder of the tag type.
     * @return An {@link Identifier} representing the tag, or {@code null} if naming is invalid.
     */
    private static Identifier getTagId(Path file, Path rootFolder) {
        Path relative = rootFolder.relativize(file);
        if (relative.getNameCount() < 2) {
            AbyssalLib.LOGGER.warning("Skipping tag file " + file + ": Must be inside a namespace folder");
            return null;
        }
        String namespace = relative.getName(0).toString();

        StringBuilder pathBuilder = new StringBuilder();
        for (int i = 1; i < relative.getNameCount(); i++) {
            if (i > 1) pathBuilder.append("/");
            pathBuilder.append(relative.getName(i).toString());
        }

        String fullPath = pathBuilder.toString();
        int lastDot = fullPath.lastIndexOf('.');
        if (lastDot > 0) fullPath = fullPath.substring(0, lastDot);

        return Identifier.of(namespace, fullPath);
    }

    /**
     * Registers a new {@link TagType} to be handled during the load cycle.
     *
     * @param <T>  Entry type.
     * @param <D>  Data type.
     * @param type The {@link TagType} to register.
     */
    public static <T, D> void register(TagType<T, D> type) {
        if (TAG_TYPES.stream().anyMatch(t -> Objects.equals(t.folder(), type.folder()))) {
            AbyssalLib.LOGGER.warning("TagType folder conflict: " + type.folder());
            return;
        }
        TAG_TYPES.add(type);
    }
}