package me.darksoul.abyssallib.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.darksoul.abyssallib.AbyssalLib;
import me.darksoul.abyssallib.registry.BuiltinRegistries;
import me.darksoul.abyssallib.util.FileUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class RecipeLoader {
    private static final File RECIPE_FOLDER = new File(AbyssalLib.getInstance().getDataFolder(), "recipes");

    public static void init() {
        if (!RECIPE_FOLDER.exists()) {
            RECIPE_FOLDER.mkdirs();
        }
    }

    public static void save(Recipe recipe) {
        JsonObject json = recipe.serialize();
        File file = new File(RECIPE_FOLDER, recipe.id.toString().replace(':', '_') + ".json");
        if (file.exists()) {
            file.delete();
        }
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(FileUtils.GSON.toJson(json));
        } catch (IOException e) {
            AbyssalLib.getInstance().getLogger().warning("Failed to save recipe: " + recipe.id);
            e.printStackTrace();
        }

    }

    public static void loadAll() {
        if (!RECIPE_FOLDER.exists()) return;

        File[] files = RECIPE_FOLDER.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null) return;

        for (File file : files) {
            try (FileReader reader = new FileReader(file)) {
                JsonElement element = JsonParser.parseReader(reader);
                if (!element.isJsonObject()) continue;
                JsonObject json = element.getAsJsonObject();

                Recipe recipe = Recipe.deserialize(json);
                BuiltinRegistries.RECIPES.register(recipe.id.toString(), (id) -> recipe);
            } catch (Exception e) {
                AbyssalLib.getInstance().getLogger().warning("Failed to load recipe from: " + file.getName());
                e.printStackTrace();
            }
        }
        AbyssalLib.getInstance().getLogger().info("Loaded " + BuiltinRegistries.RECIPES.getAll().size() + " Recipes from cache");
    }
}
