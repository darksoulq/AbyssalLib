package com.github.darksoulq.abyssallib.recipe.impl;

import com.github.darksoulq.abyssallib.recipe.Recipe;
import com.github.darksoulq.abyssallib.util.ResourceLocation;
import com.google.gson.JsonObject;
import org.bukkit.inventory.ItemStack;

import static com.github.darksoulq.abyssallib.util.Serialization.deserializeItemStack;
import static com.github.darksoulq.abyssallib.util.Serialization.serializeItemStack;

/**
 * Represents a brewing recipe in the modding framework. This extends the {@link Recipe} class and implements
 * methods for serialization and deserialization of brewing recipes.
 */
public class BrewingRecipeImpl extends Recipe {
    /**
     * The base input item (item in the three bottom slots).
     */
    public final ItemStack input;
    public final ItemStack ingredient;
    public final ItemStack result;

    /**
     * Constructs a new {@link BrewingRecipeImpl}.
     *
     * @param id         the unique identifier for the recipe
     * @param input      the base potion or input item
     * @param ingredient the ingredient used to modify the potion
     * @param result     the result of the brewing process
     */
    public BrewingRecipeImpl(ResourceLocation id, ItemStack input, ItemStack ingredient, ItemStack result) {
        super(id);
        this.input = input;
        this.ingredient = ingredient;
        this.result = result;
    }

    @Override
    public org.bukkit.inventory.Recipe toBukkit() {
        return null;
    }

    /**
     * Serialization is used to save this recipe to a JSON format.
     *
     * @return the serialized JSON representation of this brewing recipe
     */
    @Override
    public JsonObject serialize() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "brewing");
        json.addProperty("id", id.toString());
        json.add("input", serializeItemStack(input));
        json.add("ingredient", serializeItemStack(ingredient));
        json.add("result", serializeItemStack(result));
        return json;
    }

    /**
     * Deserializes a JSON object into a {@link BrewingRecipeImpl}.
     *
     * @param json the JSON object to deserialize
     * @return the deserialized {@link BrewingRecipeImpl}
     */
    public static BrewingRecipeImpl deserializeBrewing(JsonObject json) {
        String namespace = json.get("id").getAsString().split(":")[0];
        String path = json.get("id").getAsString().split(":")[1];
        ResourceLocation id = new ResourceLocation(namespace, path);
        ItemStack input = deserializeItemStack(json.getAsJsonObject("input"));
        ItemStack ingredient = deserializeItemStack(json.getAsJsonObject("ingredient"));
        ItemStack result = deserializeItemStack(json.getAsJsonObject("result"));
        return new BrewingRecipeImpl(id, input, ingredient, result);
    }

    /**
     * Initializes the BrewingRecipe deserializer by registering it with the {@link Recipe} system.
     */
    public static void init() {
        Recipe.registerDeserializer("brewing", BrewingRecipeImpl::deserializeBrewing);
    }
}
