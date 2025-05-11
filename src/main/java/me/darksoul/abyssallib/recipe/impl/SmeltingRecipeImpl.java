package me.darksoul.abyssallib.recipe.impl;

import com.google.gson.JsonObject;
import me.darksoul.abyssallib.recipe.Recipe;
import me.darksoul.abyssallib.util.ResourceLocation;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import static me.darksoul.abyssallib.util.Serialization.deserializeItemStack;
import static me.darksoul.abyssallib.util.Serialization.serializeItemStack;

/**
 * Represents a smelting recipe in the modding framework. This extends the {@link Recipe} class and implements
 * the necessary methods to convert to a Bukkit {@link FurnaceRecipe} and serialize itself.
 */
public class SmeltingRecipeImpl extends Recipe {
    public final ItemStack input;
    public final ItemStack result;
    public final float experience;
    public final int cookingTime;

    /**
     * Creates a new SmeltingRecipe with the specified parameters.
     *
     * @param id           the unique identifier for the recipe
     * @param input        the input item to be smelted
     * @param result       the resulting item after smelting
     * @param exp          the experience awarded
     * @param time         the cooking time in ticks
     */
    public SmeltingRecipeImpl(ResourceLocation id, ItemStack input, ItemStack result, float exp, int time) {
        super(id);
        this.input = input;
        this.result = result;
        this.experience = exp;
        this.cookingTime = time;
    }

    /**
     * Converts this SmeltingRecipe to a Bukkit {@link FurnaceRecipe}.
     *
     * @return the corresponding Bukkit {@link FurnaceRecipe}
     */
    @Override
    public org.bukkit.inventory.Recipe toBukkit() {
        NamespacedKey key = id.toNamespace();
        return new FurnaceRecipe(key, result.clone(), new RecipeChoice.ExactChoice(input), experience, cookingTime);
    }

    /**
     * Serializes this SmeltingRecipe to a JSON object for storage or transmission.
     *
     * @return the serialized JSON representation of this SmeltingRecipe
     */
    @Override
    public JsonObject serialize() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "smelting");
        json.addProperty("id", id.toString());
        json.add("input", serializeItemStack(input));
        json.add("result", serializeItemStack(result));
        json.addProperty("experience", experience);
        json.addProperty("cookingTime", cookingTime);
        return json;
    }

    /**
     * Deserializes a JSON object into a {@link SmeltingRecipeImpl} instance.
     *
     * @param json the JSON object to deserialize
     * @return the deserialized {@link SmeltingRecipeImpl}
     */
    public static SmeltingRecipeImpl deserializeSmelting(JsonObject json) {
        String namespace = json.get("id").getAsString().split(":")[0];
        String path = json.get("id").getAsString().split(":")[1];
        ResourceLocation id = new ResourceLocation(namespace, path);
        ItemStack input = deserializeItemStack(json.getAsJsonObject("input"));
        ItemStack result = deserializeItemStack(json.getAsJsonObject("result"));
        float experience = json.get("experience").getAsFloat();
        int cookingTime = json.get("cookingTime").getAsInt();
        return new SmeltingRecipeImpl(id, input, result, experience, cookingTime);
    }

    public static void init() {
        Recipe.registerDeserializer("smelting", SmeltingRecipeImpl::deserializeSmelting);
    }
}
