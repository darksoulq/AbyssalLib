package me.darksoul.abyssallib.recipe.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.darksoul.abyssallib.recipe.Recipe;
import me.darksoul.abyssallib.util.ResourceLocation;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.util.HashMap;
import java.util.Map;

import static me.darksoul.abyssallib.util.Serialization.deserializeItemStack;
import static me.darksoul.abyssallib.util.Serialization.serializeItemStack;

/**
 * Represents a shaped recipe in the modding framework. This extends the {@link Recipe} class and implements
 * the necessary methods to convert to a Bukkit {@link ShapedRecipe} and serialize itself.
 */
public class ShapedRecipeImpl extends Recipe {
    public final ItemStack result;
    public String[] shape;
    public final Map<Character, ItemStack> ingredients = new HashMap<>();

    /**
     * Creates a new ShapedRecipe with the specified parameters.
     *
     * @param id     the unique identifier for the recipe
     * @param result the resulting item from the recipe
     * @param shape  the shape of the crafting grid
     */
    public ShapedRecipeImpl(ResourceLocation id, ItemStack result, String... shape) {
        super(id);
        this.result = result;
        this.shape = shape;
    }
    /**
     * Creates a new ShapedRecipe with the specified parameters (without a predefined shape).
     *
     * @param id     the unique identifier for the recipe
     * @param result the resulting item from the recipe
     */
    public ShapedRecipeImpl(ResourceLocation id, ItemStack result) {
        super(id);
        this.result = result;
    }

    /**
     * Sets the shape for the recipe.
     *
     * @param shape the shape of the crafting grid
     */
    public void shape(String[] shape) {
        this.shape = shape;
    }

    /**
     * Defines an ingredient for the recipe, mapped by its key character.
     *
     * @param key  the character key for the ingredient
     * @param item the ingredient item
     * @return this ShapedRecipeImpl instance
     */
    public ShapedRecipeImpl define(char key, ItemStack item) {
        ingredients.put(key, item);
        return this;
    }

    /**
     * Gets the width of the shaped recipe based on the shape.
     *
     * @return the width of the recipe shape
     */
    public int getWidth() {
        return shape.length > 0 ? shape[0].length() : 0;
    }
    /**
     * Gets the height of the shaped recipe based on the shape.
     *
     * @return the height of the recipe shape
     */
    public int getHeight() {
        return shape.length;
    }

    /**
     * Gets the ingredient at the specified index in the shape.
     *
     * @param index the index of the ingredient
     * @return the ingredient at the specified index, or null if not present
     */
    public ItemStack getIngredient(int index) {
        int width = getWidth();
        int x = index % width;
        int y = index / width;
        if (y >= shape.length || x >= shape[y].length()) return null;
        char key = shape[y].charAt(x);
        return ingredients.getOrDefault(key, null);
    }

    /**
     * Converts this ShapedRecipe to a Bukkit {@link ShapedRecipe}.
     *
     * @return the corresponding Bukkit {@link ShapedRecipe}
     */
    @Override
    public org.bukkit.inventory.Recipe toBukkit() {
        ShapedRecipe recipe = new ShapedRecipe(id.toNamespace(), result.clone());
        recipe.shape(shape);
        for (var entry : ingredients.entrySet()) {
            recipe.setIngredient(entry.getKey(), new RecipeChoice.ExactChoice(entry.getValue()));
        }
        return recipe;
    }
    /**
     * Serializes this ShapedRecipe to a JSON object for storage or transmission.
     *
     * @return the serialized JSON representation of this ShapedRecipe
     */
    @Override
    public JsonObject serialize() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "shaped");
        json.addProperty("id", id.toString());
        json.add("result", serializeItemStack(result));

        JsonArray shapeArray = new JsonArray();
        for (String row : shape) {
            shapeArray.add(row);
        }
        json.add("shape", shapeArray);

        JsonObject ing = new JsonObject();
        for (Map.Entry<Character, ItemStack> entry : ingredients.entrySet()) {
            ing.add(entry.getKey().toString(), serializeItemStack(entry.getValue()));
        }
        json.add("ingredients", ing);

        return json;
    }

    /**
     * Deserializes a JSON object into a {@link ShapedRecipeImpl} instance.
     *
     * @param json the JSON object to deserialize
     * @return the deserialized {@link ShapedRecipeImpl}
     */
    public static ShapedRecipeImpl deserializeShaped(JsonObject json) {
        String namespace = json.get("id").getAsString().split(":")[0];
        String path = json.get("id").getAsString().split(":")[1];
        ResourceLocation id = new ResourceLocation(namespace, path);
        ItemStack result = deserializeItemStack(json.getAsJsonObject("result"));

        JsonArray shapeArray = json.getAsJsonArray("shape");
        String[] shape = new String[shapeArray.size()];
        for (int i = 0; i < shapeArray.size(); i++) {
            shape[i] = shapeArray.get(i).getAsString();
        }

        ShapedRecipeImpl recipe = new ShapedRecipeImpl(id, result, shape);

        JsonObject ing = json.getAsJsonObject("ingredients");
        for (Map.Entry<String, JsonElement> entry : ing.entrySet()) {
            recipe.define(entry.getKey().charAt(0), deserializeItemStack(entry.getValue().getAsJsonObject()));
        }

        return recipe;
    }

    /**
     * Initializes the ShapedRecipe deserializer by registering it with the {@link Recipe} system.
     */
    public static void init() {
        Recipe.registerDeserializer("shaped", ShapedRecipeImpl::deserializeShaped);
    }
}
