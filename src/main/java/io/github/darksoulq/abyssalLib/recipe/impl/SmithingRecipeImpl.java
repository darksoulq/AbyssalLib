package io.github.darksoulq.abyssalLib.recipe.impl;

import com.google.gson.JsonObject;
import io.github.darksoulq.abyssalLib.recipe.Recipe;
import io.github.darksoulq.abyssalLib.util.ResourceLocation;
import io.github.darksoulq.abyssalLib.util.Serialization;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingTransformRecipe;

/**
 * Represents a smithing recipe in the modding framework. This class extends {@link Recipe} and provides
 * methods to convert the recipe to a Bukkit {@link SmithingTransformRecipe}, as well as serialize and deserialize it.
 */
public class SmithingRecipeImpl extends Recipe {
    public final ItemStack template, base, addition, result;

    /**
     * Creates a new SmithingRecipe with the specified parameters.
     *
     * @param id        the unique identifier for the recipe
     * @param template  the template item used in the recipe
     * @param base      the base item used in the recipe
     * @param addition  the additional item used in the recipe
     * @param result    the resulting item after smithing
     */
    public SmithingRecipeImpl(ResourceLocation id, ItemStack template, ItemStack base, ItemStack addition, ItemStack result) {
        super(id);
        this.template = template;
        this.base = base;
        this.addition = addition;
        this.result = result;
    }

    /**
     * Converts this SmithingRecipe to a Bukkit {@link SmithingTransformRecipe}.
     *
     * @return the corresponding Bukkit {@link SmithingTransformRecipe}
     */
    @Override
    public org.bukkit.inventory.Recipe toBukkit() {
        NamespacedKey key = id.toNamespace();
        return new SmithingTransformRecipe(
                key,
                result.clone(),
                new RecipeChoice.ExactChoice(template),
                new RecipeChoice.ExactChoice(base),
                new RecipeChoice.ExactChoice(addition),
                true
        );
    }
    /**
     * Serializes this SmithingRecipe to a JSON object for storage or transmission.
     *
     * @return the serialized JSON representation of this SmithingRecipe
     */
    @Override
    public JsonObject serialize() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "smithing");
        json.addProperty("id", id.toString());
        json.add("template", Serialization.serializeItemStack(template));
        json.add("base", Serialization.serializeItemStack(base));
        json.add("addition", Serialization.serializeItemStack(addition));
        json.add("result", Serialization.serializeItemStack(result));
        return json;
    }

    /**
     * Deserializes a JSON object into a {@link SmithingRecipeImpl} instance.
     *
     * @param json the JSON object to deserialize
     * @return the deserialized {@link SmithingRecipeImpl}
     */
    public static SmithingRecipeImpl deserializeSmithing(JsonObject json) {
        String namespace = json.get("id").getAsString().split(":")[0];
        String path = json.get("id").getAsString().split(":")[1];
        ResourceLocation id = new ResourceLocation(namespace, path);
        ItemStack template = Serialization.deserializeItemStack(json.getAsJsonObject("template"));
        ItemStack base = Serialization.deserializeItemStack(json.getAsJsonObject("base"));
        ItemStack addition = Serialization.deserializeItemStack(json.getAsJsonObject("addition"));
        ItemStack result = Serialization.deserializeItemStack(json.getAsJsonObject("result"));
        return new SmithingRecipeImpl(id, template, base, addition, result);
    }

    /**
     * Initializes the SmithingRecipe deserializer by registering it with the {@link Recipe} system.
     */
    public static void init() {
        Recipe.registerDeserializer("smithing", SmithingRecipeImpl::deserializeSmithing);
    }
}
