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
import java.util.function.BiConsumer;

public class RecipeLoader {
    @FunctionalInterface
    public interface RecipeHandler {
        void handle(Map<Object, Object> data) throws Codec.CodecException;
    }

    private static final Map<String, RecipeHandler> HANDLERS = new HashMap<>();

    public static void registerHandler(String type, RecipeHandler handler) {
        HANDLERS.put(type, handler);
    }

    static {
        registerHandler("minecraft:shaped", data -> {
            ShapedRecipe r = Codecs.SHAPED_RECIPE.decode(YamlOps.INSTANCE, data);
            if (r != null) Registries.SHAPED_RECIPES.register(r.getKey().toString(), r);
        });
        registerHandler("minecraft:shapeless", data -> {
            ShapelessRecipe r = Codecs.SHAPELESS_RECIPE.decode(YamlOps.INSTANCE, data);
            if (r != null) Registries.SHAPELESS_RECIPES.register(r.getKey().toString(), r);
        });
        registerHandler("minecraft:transmute", data -> {
            TransmuteRecipe r = Codecs.TRANSMUTE_RECIPE.decode(YamlOps.INSTANCE, data);
            if (r != null) Registries.TRANSMUTE_RECIPES.register(r.getKey().toString(), r);
        });
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
        registerHandler("minecraft:smithing_transform", data -> {
            SmithingTransformRecipe r = Codecs.SMITHING_TRANSFORM_RECIPE.decode(YamlOps.INSTANCE, data);
            if (r != null) Registries.SMITHING_TRANSFORM_RECIPES.register(r.getKey().toString(), r);
        });
        registerHandler("minecraft:stonecutting", data -> {
            StonecuttingRecipe r = Codecs.STONECUTTING_RECIPE.decode(YamlOps.INSTANCE, data);
            if (r != null) Registries.STONECUTTING_RECIPES.register(r.getKey().toString(), r);
        });
        registerHandler("minecraft:potion_mix", data -> {
            PotionMix r = Codecs.POTION_MIX.decode(YamlOps.INSTANCE, data);
            if (r != null) Registries.POTION_MIXES.register(r.getKey().toString(), r);
        });
    }

    public static void loadFolder(File folder) {
        if (!folder.exists() || !folder.isDirectory()) return;
        File[] files = folder.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (file.isDirectory()) loadFolder(file);
            else if (file.getName().endsWith(".yml") || file.getName().endsWith(".yaml")) {
                loadFile(file);
            }
        }
    }

    public static void loadFolder(Plugin plugin, String resourcePath) {
        List<String> files = FileUtils.getFilePathList(plugin, resourcePath);
        for (String file : files) {
            try (InputStream in = plugin.getResource(file)) {
                if (in != null) loadFile(in);
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load recipe resource: " + file + " - " + e.getMessage());
            }
        }
    }

    public static void loadFile(File file) {
        try (InputStream in = new FileInputStream(file)) {
            loadFile(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadFile(InputStream in) throws Codec.CodecException {
        Object root = YamlOps.INSTANCE.parse(in);
        if (root instanceof List<?> list) {
            decode(list);
        } else {
            decode(Collections.singletonList(root));
        }
    }

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
