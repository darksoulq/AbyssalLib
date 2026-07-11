package com.github.darksoulq.abyssallib.world.recipe;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.ops.JsonOps;
import com.github.darksoulq.abyssallib.common.serialization.ops.YamlOps;
import com.github.darksoulq.abyssallib.common.util.FileUtils;
import com.github.darksoulq.abyssallib.server.event.custom.server.RecipeReloadEvent;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.recipe.type.DisabledRecipe;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

/**
 * A utility class for loading, parsing, and registering Minecraft recipes from external files.
 * <p>
 * This class supports standard Minecraft recipe types (Shaped, Shapeless, Furnace, etc.) as well
 * as custom types.
 */
public class RecipeLoader {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Recursively scans a folder and loads all JSON and YAML files found as recipes.
     *
     * @param folder The {@link File} directory to scan.
     * @return The amount of successfully parsed and executed configuration paths.
     */
    public static int loadFolder(File folder) {
        int loaded = 0;
        if (!folder.exists() || !folder.isDirectory()) return loaded;
        File[] files = folder.listFiles();
        if (files == null) return loaded;
        for (File file : files) {
            if (file.isDirectory()) {
                loaded += loadFolder(file);
            } else if (file.getName().endsWith(".yml") || file.getName().endsWith(".yaml") || file.getName().endsWith(".json")) {
                loadFile(file);
                loaded++;
            }
        }
        return loaded;
    }

    /**
     * Loads recipes from a specific resource path within a plugin's JAR.
     *
     * @param plugin       The {@link Plugin} instance.
     * @param resourcePath The internal path (e.g., "recipes/").
     * @return The amount of successfully parsed and executed configuration paths.
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
     * Loads a single recipe file from the file system.
     *
     * @param file The YAML or JSON {@link File}.
     */
    public static void loadFile(File file) {
        try (InputStream in = new FileInputStream(file)) {
            if (file.getName().endsWith(".json")) {
                loadJson(in, file.getName());
            } else {
                loadYaml(in, file.getName());
            }
        } catch (Exception e) {
            AbyssalLib.LOGGER.warning("Failed to load recipe file: " + file.getName());
            e.printStackTrace();
        }
    }

    /**
     * Loads a single recipe from a plugin resource.
     *
     * @param plugin       The {@link Plugin} owning the resource.
     * @param resourcePath The path to the file.
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
            plugin.getLogger().warning("Failed to load recipe resource: " + resourcePath + " - " + e.getMessage());
        }
    }

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
            AbyssalLib.LOGGER.warning("Failed to parse JSON recipe from " + source);
            e.printStackTrace();
        }
    }

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
            AbyssalLib.LOGGER.warning("Failed to parse YAML recipe from " + source);
            e.printStackTrace();
        }
    }

    private static <D> void decode(DynamicOps<D> ops, D input, String source) {
        DataResult<Void> result = ops.getMap(input)
            .map(DataResult::success)
            .orElseGet(() -> DataResult.error("Expected structured map input for recipe."))
            .flatMap(map -> {
                D idObj = map.get(ops.createString("id"));
                if (idObj == null) {
                    return DataResult.error("Recipe missing required 'id' key.");
                }

                return Codecs.KEY.decode(ops, idObj).flatMap(id -> {
                    D disabledObj = map.get(ops.createString("disabled"));
                    boolean disabled = disabledObj != null && Codecs.BOOLEAN.decode(ops, disabledObj).result().or(() -> {
                        AbyssalLib.LOGGER.severe("Invalid boolean value on disabled in " + source);
                        return Optional.of(false);
                    }).get();

                    if (disabled) {
                        if (Registries.RECIPES.contains(id.asString())) {
                            Registries.RECIPES.remove(id.asString());
                        }
                        Registries.RECIPES.register(id.asString(), new DisabledRecipe(NamespacedKey.fromString(id.asString())));
                        return DataResult.success(null);
                    }

                    D typeObj = map.get(ops.createString("type"));
                    if (typeObj == null) {
                        return DataResult.error("Recipe missing required 'type' discriminator key.");
                    }

                    return CustomRecipe.CODEC.decode(ops, input).flatMap(recipe -> {
                        if (Registries.RECIPES.contains(recipe.getKey().asString())) {
                            Registries.RECIPES.remove(recipe.getKey().asString());
                        }
                        Registries.RECIPES.register(recipe.getKey().asString(), recipe);
                        return DataResult.success(null);
                    });
                });
            });

        if (result.isError()) {
            AbyssalLib.LOGGER.warning("Encountered failure while decoding recipe data in " + source + " -> " + result.error().orElse("Unknown error"));
        } else if (result.isPartial()) {
            result.warnings().forEach(w -> AbyssalLib.LOGGER.warning("Warning mapping recipe in " + source + ": " + w.message()));
        }
    }

    /**
     * Finalizes the recipe loading process by injecting all registered recipes into the Bukkit server.
     * <p>
     * This method iterates through all internal registries and calls {@link Bukkit#addRecipe(org.bukkit.inventory.Recipe)}
     * or adds potion mixes to the brewing system.
     */
    @ApiStatus.Internal
    public static void reload() {
        RecipeReloadEvent event = new RecipeReloadEvent();
        Bukkit.getPluginManager().callEvent(event);

        for (CustomRecipe recipe : Registries.RECIPES.getAll().values()) {
            registerToBukkit(recipe);
        }
    }

    /**
     * Evaluates and routes a loaded custom recipe instance into the vanilla server recipe manager.
     *
     * @param recipe The recipe to append to the active Bukkit state map.
     */
    public static void registerToBukkit(CustomRecipe recipe) {
        if (recipe instanceof DisabledRecipe || recipe.replace()) {
            Bukkit.removeRecipe(NamespacedKey.fromString(recipe.getKey().asString()));
            try {
                Bukkit.getPotionBrewer().removePotionMix(NamespacedKey.fromString(recipe.getKey().asString()));
            } catch (Exception ignored) {
            }
        }

        switch (recipe) {
            case BukkitRecipeProvider provider -> Bukkit.addRecipe(provider.toBukkit(), true);
            case PotionMixProvider provider -> Bukkit.getPotionBrewer().addPotionMix(provider.toPotionMix());
            default -> {
            }
        }
    }
}