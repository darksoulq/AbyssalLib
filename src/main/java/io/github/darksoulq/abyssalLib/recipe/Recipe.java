package io.github.darksoulq.abyssalLib.recipe;

import com.google.gson.JsonObject;
import io.github.darksoulq.abyssalLib.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Represents a generic recipe in the modding framework. Specific recipe types should extend this class and
 * implement the methods to convert to Bukkit recipes and serialize themselves.
 */
public abstract class Recipe {
    public ResourceLocation id;

    /**
     * Creates a new recipe with the specified identifier.
     *
     * @param id the unique identifier for the recipe
     */
    public Recipe(ResourceLocation id) {
        this.id = id;
    }

    /**
     * Converts this recipe to a Bukkit recipe.
     *
     * @return the corresponding Bukkit recipe
     */
    public abstract org.bukkit.inventory.Recipe toBukkit();
    /**
     * Serializes this recipe to a JSON object for storage or transmission.
     *
     * @return the serialized JSON representation of this recipe
     */
    public abstract JsonObject serialize();

    /**
     * A map that holds deserializers for different recipe types.
     */
    private static final Map<String, Function<JsonObject, Recipe>> DESERIALIZERS = new HashMap<>();

    /**
     * Registers a deserializer for a specific recipe type.
     *
     * @param type       the type of recipe being deserialized
     * @param deserializer the function that will handle deserialization of the recipe
     */
    public static void registerDeserializer(String type, Function<JsonObject, Recipe> deserializer) {
        DESERIALIZERS.put(type, deserializer);
    }

    /**
     * Deserializes a JSON object into a specific recipe based on its type.
     *
     * @param json the JSON object to deserialize
     * @return the deserialized recipe
     * @throws IllegalArgumentException if the recipe type is unknown
     */
    public static Recipe deserialize(JsonObject json) {
        String type = json.get("type").getAsString();
        Function<JsonObject, Recipe> deserializer = DESERIALIZERS.get(type);
        if (deserializer == null) {
            throw new IllegalArgumentException("Unknown recipe type: " + type);
        }
        return deserializer.apply(json);
    }
}
