package com.github.darksoulq.abyssallib.recipe.impl;

import com.github.darksoulq.abyssallib.recipe.Recipe;
import com.github.darksoulq.abyssallib.util.ResourceLocation;
import com.github.darksoulq.abyssallib.util.Serialization;
import com.google.gson.JsonObject;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.StonecuttingRecipe;

/**
 * Represents a stonecutting recipe in the modding framework. This class extends {@link Recipe} and provides
 * methods to convert the recipe to a Bukkit {@link StonecuttingRecipe}, as well as serialize and deserialize it.
 */
public class StonecuttingRecipeImpl extends Recipe {
    public final ItemStack input;
    public final ItemStack result;

    /**
     * Creates a new StonecuttingRecipe with the specified parameters.
     *
     * @param id     the unique identifier for the recipe
     * @param input  the input item used in the recipe
     * @param result the resulting item after stonecutting
     */
    public StonecuttingRecipeImpl(ResourceLocation id, ItemStack input, ItemStack result) {
        super(id);
        this.input = input;
        this.result = result;
    }

    /**
     * Converts this StonecuttingRecipe to a Bukkit {@link StonecuttingRecipe}.
     *
     * @return the corresponding Bukkit {@link StonecuttingRecipe}
     */
    @Override
    public org.bukkit.inventory.Recipe toBukkit() {
        NamespacedKey key = id.toNamespace();
        return new StonecuttingRecipe(key, result.clone(), new RecipeChoice.ExactChoice(input));
    }
    /**
     * Serializes this StonecuttingRecipe to a JSON object for storage or transmission.
     *
     * @return the serialized JSON representation of this StonecuttingRecipe
     */
    @Override
    public JsonObject serialize() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "stonecutting");
        json.addProperty("id", id.toString());
        json.add("input", Serialization.serializeItemStack(input));
        json.add("result", Serialization.serializeItemStack(result));
        return json;
    }

    /**
     * Deserializes a JSON object into a {@link StonecuttingRecipeImpl} instance.
     *
     * @param json the JSON object to deserialize
     * @return the deserialized {@link StonecuttingRecipeImpl}
     */
    public static StonecuttingRecipeImpl deserializeStonecutting(JsonObject json) {
        String namespace = json.get("id").getAsString().split(":")[0];
        String path = json.get("id").getAsString().split(":")[1];
        ResourceLocation id = new ResourceLocation(namespace, path);
        ItemStack input = Serialization.deserializeItemStack(json.getAsJsonObject("input"));
        ItemStack result = Serialization.deserializeItemStack(json.getAsJsonObject("result"));
        return new StonecuttingRecipeImpl(id, input, result);
    }

    /**
     * Initializes the StonecuttingRecipe deserializer by registering it with the {@link Recipe} system.
     */
    public static void init() {
        Recipe.registerDeserializer("stonecutting", StonecuttingRecipeImpl::deserializeStonecutting);
    }
}
