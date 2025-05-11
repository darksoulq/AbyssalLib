package me.darksoul.abyssallib.recipe.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.darksoul.abyssallib.AbyssalLib;
import me.darksoul.abyssallib.recipe.Recipe;
import me.darksoul.abyssallib.util.ResourceLocation;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.ArrayList;
import java.util.List;

import static me.darksoul.abyssallib.util.Serialization.deserializeItemStack;
import static me.darksoul.abyssallib.util.Serialization.serializeItemStack;

/**
 * Represents a shapeless recipe in the modding framework. This extends the {@link Recipe} class and implements
 * the necessary methods to convert to a Bukkit {@link ShapelessRecipe} and serialize itself.
 */
public class ShapelessRecipeImpl extends Recipe {
    public final ItemStack result;
    public final List<ItemStack> ingredients = new ArrayList<>();

    /**
     * Creates a new ShapelessRecipe with the specified parameters.
     *
     * @param id     the unique identifier for the recipe
     * @param result the resulting item from the recipe
     */
    public ShapelessRecipeImpl(ResourceLocation id, ItemStack result) {
        super(id);
        this.result = result;
    }

    /**
     * Adds an ingredient to the shapeless recipe.
     * A maximum of 6 ingredients can be added to a shapeless recipe.
     *
     * @param item the ingredient item to add
     * @return this ShapelessRecipeImpl instance
     */
    public ShapelessRecipeImpl addIngredient(ItemStack item) {
        if (ingredients.size() >= 6) {
            AbyssalLib.getInstance().getLogger().warning("Failed to add ingredient, only 6 ingredients can be added!");
            return this;
        }
        ingredients.add(item);
        return this;
    }

    /**
     * Converts this ShapelessRecipe to a Bukkit {@link ShapelessRecipe}.
     *
     * @return the corresponding Bukkit {@link ShapelessRecipe}
     */
    @Override
    public org.bukkit.inventory.Recipe toBukkit() {
        ShapelessRecipe recipe = new ShapelessRecipe(id.toNamespace(), result.clone());
        ingredients.forEach(ingredient -> recipe.addIngredient(new RecipeChoice.ExactChoice(ingredient)));
        return recipe;
    }

    /**
     * Serializes this ShapelessRecipe to a JSON object for storage or transmission.
     *
     * @return the serialized JSON representation of this ShapelessRecipe
     */
    @Override
    public JsonObject serialize() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "shapeless");
        json.addProperty("id", id.toString());
        json.add("result", serializeItemStack(result));

        JsonArray ingredientsArray = new JsonArray();
        ingredients.forEach(ingredient -> ingredientsArray.add(serializeItemStack(ingredient)));
        json.add("ingredients", ingredientsArray);

        return json;
    }

    /**
     * Deserializes a JSON object into a {@link ShapelessRecipeImpl} instance.
     *
     * @param json the JSON object to deserialize
     * @return the deserialized {@link ShapelessRecipeImpl}
     */
    public static ShapelessRecipeImpl deserializeShapeless(JsonObject json) {
        String namespace = json.get("id").getAsString().split(":")[0];
        String path = json.get("id").getAsString().split(":")[1];
        ResourceLocation id = new ResourceLocation(namespace, path);
        ItemStack result = deserializeItemStack(json.getAsJsonObject("result"));

        ShapelessRecipeImpl recipe = new ShapelessRecipeImpl(id, result);

        JsonArray ingredientsArray = json.getAsJsonArray("ingredients");
        for (int i = 0; i < ingredientsArray.size(); i++) {
            recipe.addIngredient(deserializeItemStack(ingredientsArray.get(i).getAsJsonObject()));
        }

        return recipe;
    }

    /**
     * Initializes the ShapelessRecipe deserializer by registering it with the {@link Recipe} system.
     */
    public static void init() {
        Recipe.registerDeserializer("shapeless", ShapelessRecipeImpl::deserializeShapeless);
    }
}
