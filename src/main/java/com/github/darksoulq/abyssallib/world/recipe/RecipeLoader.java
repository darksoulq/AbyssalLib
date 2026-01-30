package com.github.darksoulq.abyssallib.world.recipe;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.ops.YamlOps;
import com.github.darksoulq.abyssallib.common.util.FileUtils;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import io.papermc.paper.potion.PotionMix;
import org.bukkit.Bukkit;
import org.bukkit.inventory.*;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * A utility class for loading, parsing, and registering Minecraft recipes from external files.
 * <p>
 * This class supports standard Minecraft recipe types (Shaped, Shapeless, Furnace, etc.) as well
 * as custom types. It uses a handler-based system to decode
 * YAML data into Bukkit or library-specific recipe objects.
 */
public class RecipeLoader {

    /**
     * Functional interface for handling the conversion of raw YAML data into a recipe object.
     */
    @FunctionalInterface
    public interface RecipeHandler {
        /**
         * Processes raw recipe data and registers it to the appropriate registry.
         *
         * @param data The raw key-value mapping from the YAML file.
         * @throws Codec.CodecException If the data does not match the expected recipe format.
         */
        void handle(Map<Object, Object> data) throws Codec.CodecException;
    }

    /** Map containing registered handlers for specific recipe type identifiers. */
    private static final Map<String, RecipeHandler> HANDLERS = new HashMap<>();

    /**
     * Registers a custom handler for a specific recipe type.
     *
     * @param type    The string identifier for the recipe type (e.g., "minecraft:shaped").
     * @param handler The {@link RecipeHandler} logic to process this type.
     */
    public static void registerHandler(String type, RecipeHandler handler) {
        HANDLERS.put(type, handler);
    }

    static {
        // Standard Crafting Recipes
        registerHandler("minecraft:shaped", data -> {
            ShapedRecipe r = Codecs.SHAPED_RECIPE.decode(YamlOps.INSTANCE, data);
            if (r != null) Registries.SHAPED_RECIPES.register(r.getKey().toString(), r);
        });
        registerHandler("minecraft:shapeless", data -> {
            ShapelessRecipe r = Codecs.SHAPELESS_RECIPE.decode(YamlOps.INSTANCE, data);
            if (r != null) Registries.SHAPELESS_RECIPES.register(r.getKey().toString(), r);
        });

        // AbyssalLib Custom Recipes
        registerHandler("minecraft:transmute", data -> {
            TransmuteRecipe r = Codecs.TRANSMUTE_RECIPE.decode(YamlOps.INSTANCE, data);
            if (r != null) Registries.TRANSMUTE_RECIPES.register(r.getKey().toString(), r);
        });

        // Smelting/Cooking Recipes
        registerHandler("minecraft:furnace", data -> {
            FurnaceRecipe r = Codecs.FURNACE_RECIPE.decode(YamlOps.INSTANCE, data);
            if (r != null) Registries.FURNACE_RECIPES.register(r.getKey().toString(), r);
        });
        registerHandler("minecraft:blasting", data -> {
            BlastingRecipe r = Codecs.BLASTING_RECIPE.decode(YamlOps.INSTANCE, data);
            if (r != null) Registries.BLASTING_RECIPES.register(r.getKey().toString(), r);
        });
        registerHandler("minecraft:smoking", data -> {
            SmokingRecipe r = Codecs.SMOKING_RECIPE.decode(YamlOps.INSTANCE, data);
            if (r != null) Registries.SMOKING_RECIPES.register(r.getKey().toString(), r);
        });
        registerHandler("minecraft:campfire", data -> {
            CampfireRecipe r = Codecs.CAMPFIRE_RECIPE.decode(YamlOps.INSTANCE, data);
            if (r != null) Registries.CAMPFIRE_RECIPES.register(r.getKey().toString(), r);
        });

        // Advanced Table Recipes
        registerHandler("minecraft:smithing_transform", data -> {
            SmithingTransformRecipe r = Codecs.SMITHING_TRANSFORM_RECIPE.decode(YamlOps.INSTANCE, data);
            if (r != null) Registries.SMITHING_TRANSFORM_RECIPES.register(r.getKey().toString(), r);
        });
        registerHandler("minecraft:stonecutting", data -> {
            StonecuttingRecipe r = Codecs.STONECUTTING_RECIPE.decode(YamlOps.INSTANCE, data);
            if (r != null) Registries.STONECUTTING_RECIPES.register(r.getKey().toString(), r);
        });

        // Alchemy/Brewing Recipes
        registerHandler("minecraft:potion_mix", data -> {
            PotionMix r = Codecs.POTION_MIX.decode(YamlOps.INSTANCE, data);
            if (r != null) Registries.POTION_MIXES.register(r.getKey().toString(), r);
        });
    }

    /**
     * Recursively scans a folder and loads all YAML files found as recipes.
     *
     * @param folder The {@link File} directory to scan.
     */
    public static void loadFolder(File folder) {
        if (!folder.exists() || !folder.isDirectory()) return;
        File[] files = folder.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (file.isDirectory()) loadFolder(file);
            else if (file.getName().endsWith(".yml") || file.getName().endsWith(".yaml")) {
                load(file);
            }
        }
    }

    /**
     * Loads recipes from a specific resource path within a plugin's JAR.
     *
     * @param plugin       The {@link Plugin} instance.
     * @param resourcePath The internal path (e.g., "recipes/").
     */
    public static void loadFolder(Plugin plugin, String resourcePath) {
        List<String> files = FileUtils.getFilePathList(plugin, resourcePath);
        for (String file : files) {
            loadResource(plugin, file);
        }
    }

    /**
     * Loads a single recipe file from the file system.
     *
     * @param file The YAML {@link File}.
     */
    public static void load(File file) {
        try (InputStream in = new FileInputStream(file)) {
            load(in);
        } catch (Exception e) {
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
            if (in != null) load(in);
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load recipe resource: " + resourcePath + " - " + e.getMessage());
        }
    }

    /**
     * Parses an input stream into one or more recipe definitions.
     *
     * @param in The {@link InputStream} of the YAML content.
     * @throws Codec.CodecException If parsing fails.
     */
    public static void load(InputStream in) throws Codec.CodecException {
        Object root = YamlOps.INSTANCE.parse(in);
        if (root instanceof List<?> list) {
            decode(list);
        } else {
            decode(Collections.singletonList(root));
        }
    }

    /**
     * Iterates through a list of raw objects and delegates them to registered handlers.
     *
     * @param recipes List of raw recipe data.
     * @throws Codec.CodecException If a handler fails to decode the data.
     */
    private static void decode(List<?> recipes) throws Codec.CodecException {
        for (Object recipe : recipes) {
            if (!(recipe instanceof Map<?, ?> map)) continue;
            Object typeObj = map.get("type");
            if (!(typeObj instanceof String type)) continue;

            Map<Object, Object> data = new LinkedHashMap<>(map);
            data.remove("type");

            RecipeHandler handler = HANDLERS.get(type);
            if (handler != null) {
                handler.handle(data);
            }
        }
    }

    /**
     * Finalizes the recipe loading process by injecting all registered recipes into the Bukkit server.
     * <p>
     * This method iterates through all internal registries and calls {@link Bukkit#addRecipe(Recipe)}
     * or adds potion mixes to the brewing system.
     */
    @ApiStatus.Internal
    public static void reload() {
        Registries.SHAPED_RECIPES.getAll().values().forEach(v -> Bukkit.addRecipe(v, true));
        Registries.SHAPELESS_RECIPES.getAll().values().forEach(v -> Bukkit.addRecipe(v, true));
        Registries.TRANSMUTE_RECIPES.getAll().values().forEach(v -> Bukkit.addRecipe(v, true));
        Registries.FURNACE_RECIPES.getAll().values().forEach(v -> Bukkit.addRecipe(v, true));
        Registries.BLASTING_RECIPES.getAll().values().forEach(v -> Bukkit.addRecipe(v, true));
        Registries.SMOKING_RECIPES.getAll().values().forEach(v -> Bukkit.addRecipe(v, true));
        Registries.CAMPFIRE_RECIPES.getAll().values().forEach(v -> Bukkit.addRecipe(v, true));
        Registries.STONECUTTING_RECIPES.getAll().values().forEach(v -> Bukkit.addRecipe(v, true));
        Registries.SMITHING_TRANSFORM_RECIPES.getAll().values().forEach(v -> Bukkit.addRecipe(v, true));
        Registries.POTION_MIXES.getAll().values().forEach(p -> Bukkit.getPotionBrewer().addPotionMix(p));
    }
}