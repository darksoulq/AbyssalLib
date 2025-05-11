package me.darksoul.abyssallib.recipe.impl;

import com.google.gson.JsonObject;
import me.darksoul.abyssallib.recipe.Recipe;
import me.darksoul.abyssallib.util.ResourceLocation;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import static me.darksoul.abyssallib.util.Serialization.deserializeItemStack;
import static me.darksoul.abyssallib.util.Serialization.serializeItemStack;

/**
 * Represents a campfire recipe in the modding framework. This extends the {@link Recipe} class and implements
 * the necessary methods to convert to a Bukkit {@link CampfireRecipe} and serialize itself.
 */
public class CampfireRecipeImpl extends Recipe {
    public final ItemStack input;
    public final ItemStack result;
    public final float experience;
    public final int cookingTime;

    /**
     * Creates a new CampfireRecipe with the specified parameters.
     *
     * @param id         the unique identifier for the recipe
     * @param input      the input item for the recipe
     * @param result     the resulting item from the recipe
     * @param exp        the experience gained from the recipe
     * @param time       the cooking time (in ticks)
     */
    public CampfireRecipeImpl(ResourceLocation id, ItemStack input, ItemStack result, float exp, int time) {
        super(id);
        this.input = input;
        this.result = result;
        this.experience = exp;
        this.cookingTime = time;
    }

    /**
     * Converts this CampfireRecipe to a Bukkit {@link CampfireRecipe}.
     *
     * @return the corresponding Bukkit {@link CampfireRecipe}
     */
    @Override
    public org.bukkit.inventory.Recipe toBukkit() {
        NamespacedKey key = id.toNamespace();
        return new CampfireRecipe(key, result.clone(), new RecipeChoice.ExactChoice(input), experience, cookingTime);
    }

    /**
     * Serializes this CampfireRecipe to a JSON object for storage or transmission.
     *
     * @return the serialized JSON representation of this CampfireRecipe
     */
    @Override
    public JsonObject serialize() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "campfire");
        json.addProperty("id", id.toString());
        json.add("input", serializeItemStack(input));
        json.add("result", serializeItemStack(result));
        json.addProperty("experience", experience);
        json.addProperty("cookingTime", cookingTime);
        return json;
    }

    /**
     * Deserializes a JSON object into a {@link CampfireRecipeImpl} instance.
     *
     * @param json the JSON object to deserialize
     * @return the deserialized {@link CampfireRecipeImpl}
     */
    public static CampfireRecipeImpl deserializeCampfire(JsonObject json) {
        String namespace = json.get("id").getAsString().split(":")[0];
        String path = json.get("id").getAsString().split(":")[1];
        ResourceLocation id = new ResourceLocation(namespace, path);
        ItemStack input = deserializeItemStack(json.getAsJsonObject("input"));
        ItemStack result = deserializeItemStack(json.getAsJsonObject("result"));
        float experience = json.get("experience").getAsFloat();
        int time = json.get("cookingTime").getAsInt();
        return new CampfireRecipeImpl(id, input, result, experience, time);
    }

    /**
     * Initializes the CampfireRecipe deserializer by registering it with the {@link Recipe} system.
     */
    public static void init() {
        Recipe.registerDeserializer("campfire", CampfireRecipeImpl::deserializeCampfire);
    }
}
