package com.github.darksoulq.abyssallib.world.data.tag;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.ops.JsonOps;
import com.github.darksoulq.abyssallib.common.serialization.ops.YamlOps;
import com.github.darksoulq.abyssallib.common.util.FileUtils;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import net.kyori.adventure.key.Key;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A utility class responsible for loading, parsing, and registering data tags from external files.
 * <p>
 * This loader supports deep directory scanning and can parse both JSON and YAML file formats.
 * Files must define a {@code type} (to resolve the {@link TagType} and its codec) and an {@code id}
 * (to register the instantiated {@link Tag} into the central registry). It automatically handles
 * appending data to existing tags and recursively resolving cross-tag inclusions.
 */
public class TagLoader {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Map<String, List<String>> PENDING_INCLUDES = new HashMap<>();

    /**
     * Recursively scans a system folder and loads all JSON and YAML files found as tags.
     *
     * @param folder The root {@link File} directory to scan.
     * @return The total number of tag files successfully processed.
     */
    public static int loadFolder(File folder) {
        int loaded = 0;
        if (!folder.exists() || !folder.isDirectory()) return loaded;
        
        File[] files = folder.listFiles();
        if (files == null) return loaded;
        
        for (File file : files) {
            if (file.isDirectory()) {
                loaded += loadFolder(file);
            } else {
                String name = file.getName();
                if (name.endsWith(".json") || name.endsWith(".yml") || name.endsWith(".yaml")) {
                    loadFile(file);
                    loaded++;
                }
            }
        }
        return loaded;
    }

    /**
     * Deeply scans and loads tags from a specific resource path within a plugin's embedded JAR file.
     *
     * @param plugin       The {@link Plugin} instance owning the resources.
     * @param resourcePath The internal path directory (e.g., "tags/").
     * @return The total number of tag resources successfully processed.
     */
    public static int loadFolder(Plugin plugin, String resourcePath) {
        int loaded = 0;
        List<String> files = FileUtils.getFilePathList(plugin, resourcePath);
        for (String file : files) {
            if (file.endsWith(".json") || file.endsWith(".yml") || file.endsWith(".yaml")) {
                loadResource(plugin, file);
                loaded++;
            }
        }
        return loaded;
    }

    /**
     * Loads a single tag definition from the local file system.
     *
     * @param file The {@link File} to read.
     */
    public static void loadFile(File file) {
        try (InputStream in = new FileInputStream(file)) {
            if (file.getName().endsWith(".json")) {
                loadJson(in, file.getName());
            } else {
                loadYaml(in, file.getName());
            }
        } catch (Exception e) {
            AbyssalLib.LOGGER.warning("Failed to load tag file: " + file.getName());
            e.printStackTrace();
        }
    }

    /**
     * Loads a single tag definition from an internal plugin resource.
     *
     * @param plugin       The {@link Plugin} holding the resource.
     * @param resourcePath The relative path to the resource file.
     */
    public static void loadResource(Plugin plugin, String resourcePath) {
        try (InputStream in = plugin.getResource(resourcePath)) {
            if (in != null) {
                if (resourcePath.endsWith(".json")) {
                    loadJson(in, resourcePath);
                } else {
                    loadYaml(in, resourcePath);
                }
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load tag resource: " + resourcePath);
        }
    }

    /**
     * Parses an input stream assuming JSON formatting.
     *
     * @param in     The {@link InputStream} containing JSON data.
     * @param source The source identifier used for logging errors.
     */
    private static void loadJson(InputStream in, String source) {
        try {
            JsonNode root = MAPPER.readTree(in);
            if (root.isArray()) {
                for (JsonNode node : root) {
                    decode(JsonOps.INSTANCE, node, source);
                }
            } else {
                decode(JsonOps.INSTANCE, root, source);
            }
        } catch (Exception e) {
            AbyssalLib.LOGGER.warning("Failed to parse JSON tag from " + source);
            e.printStackTrace();
        }
    }

    /**
     * Parses an input stream assuming YAML formatting.
     *
     * @param in     The {@link InputStream} containing YAML data.
     * @param source The source identifier used for logging errors.
     */
    private static void loadYaml(InputStream in, String source) {
        try {
            Object root = YamlOps.INSTANCE.parse(in);
            if (root instanceof List<?> list) {
                for (Object node : list) {
                    decode(YamlOps.INSTANCE, node, source);
                }
            } else {
                decode(YamlOps.INSTANCE, root, source);
            }
        } catch (Exception e) {
            AbyssalLib.LOGGER.warning("Failed to parse YAML tag from " + source);
            e.printStackTrace();
        }
    }

    /**
     * Core decoding logic that transforms raw data maps into functional {@link Tag} objects.
     * <p>
     * Extracts the tag type and unique ID from the data. Decodes standard values using the type's
     * designated codec, and defers string-based includes to be resolved after all files are parsed.
     *
     * @param ops    The {@link DynamicOps} protocol matching the input type.
     * @param input  The raw data object mapped to the given dynamic ops.
     * @param source The source identifier used for logging errors.
     * @param <D>    The data serialization format type.
     * @param <T>    The specific type of the tag's entries.
     * @param <V>    The object structure verified against the tag.
     */
    @SuppressWarnings("unchecked")
    private static <D, T, V> void decode(DynamicOps<D> ops, D input, String source) {
        try {
            Map<D, D> map = ops.getMap(input).orElse(null);
            if (map == null) return;

            D typeObj = map.get(ops.createString("type"));
            D idObj = map.get(ops.createString("id"));

            if (typeObj == null || idObj == null) {
                AbyssalLib.LOGGER.warning("Tag definition missing required 'type' or 'id' key in " + source);
                return;
            }

            String typeStr = Codecs.STRING.decode(ops, typeObj);
            String idStr = Codecs.STRING.decode(ops, idObj);

            TagType<T, V> type = (TagType<T, V>) Registries.TAG_TYPES.get(typeStr);
            if (type == null) {
                AbyssalLib.LOGGER.warning("Unknown tag type '" + typeStr + "' referenced in " + source);
                return;
            }

            Key id = Key.key(idStr);
            Tag<T, V> tag = type.create(id);

            D valuesObj = map.get(ops.createString("values"));
            if (valuesObj != null) {
                List<T> values = type.codec().list().decode(ops, valuesObj);
                if (values != null) {
                    values.forEach(tag::add);
                }
            }

            D includesObj = map.get(ops.createString("includes"));
            if (includesObj != null) {
                List<String> includes = Codecs.STRING.list().decode(ops, includesObj);
                if (includes != null) {
                    PENDING_INCLUDES.computeIfAbsent(idStr, k -> new ArrayList<>()).addAll(includes);
                }
            }

            Tag<T, V> existing = (Tag<T, V>) Registries.TAGS.get(idStr);
            if (existing != null && existing.getType() == type) {
                tag.getValues().forEach(existing::add);
            } else {
                Registries.TAGS.register(idStr, tag);
            }

        } catch (Exception e) {
            AbyssalLib.LOGGER.warning("Encountered failure while decoding tag data in " + source);
            e.printStackTrace();
        }
    }

    /**
     * Finalizes the tag loading phase by binding interdependent inclusions.
     * <p>
     * Iterates through all parsed {@code "includes"} lists to dynamically link
     * parent and child tags. Any unresolved or mismatched types are logged as warnings.
     */
    public static void resolveIncludes() {
        for (Map.Entry<String, List<String>> entry : PENDING_INCLUDES.entrySet()) {
            Tag<?, ?> parent = Registries.TAGS.get(entry.getKey());
            if (parent == null) continue;

            for (String includeId : entry.getValue()) {
                Tag<?, ?> included = Registries.TAGS.get(includeId);
                
                if (included != null) {
                    if (parent.getType() == included.getType()) {
                        unsafeInclude(parent, included);
                    } else {
                        AbyssalLib.LOGGER.warning("Tag type mismatch detected when including '" + includeId + "' into parent '" + entry.getKey() + "'. Inclusion aborted.");
                    }
                } else {
                    AbyssalLib.LOGGER.warning("Attempted to include an unknown or non-existent tag: '" + includeId + "' in parent '" + entry.getKey() + "'.");
                }
            }
        }
        
        PENDING_INCLUDES.clear();
    }

    /**
     * Bypasses generic type erasure warnings to safely map two previously-validated tags together.
     *
     * @param parent The parent {@link Tag} consuming the values.
     * @param child  The child {@link Tag} being included.
     * @param <T>    The matching data entry type.
     * @param <D>    The matching evaluation logic type.
     */
    @SuppressWarnings("unchecked")
    private static <T, D> void unsafeInclude(Tag<?, ?> parent, Tag<?, ?> child) {
        ((Tag<T, D>) parent).include((Tag<T, D>) child);
    }
}