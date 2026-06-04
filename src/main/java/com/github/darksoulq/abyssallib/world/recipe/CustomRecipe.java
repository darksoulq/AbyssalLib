package com.github.darksoulq.abyssallib.world.recipe;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a custom recipe structure defining the transformation of inputs to an output.
 */
public interface CustomRecipe {

    /**
     * Polymorphic codec for serializing and deserializing custom recipes.
     * It uses the "type" field to delegate serialization logic to the registered {@link RecipeType}.
     */
    Codec<CustomRecipe> CODEC = Codec.dispatch(
        CustomRecipe.class,
        "type",
        Codecs.STRING,
        recipe -> {
            String typeId = Registries.RECIPE_TYPES.getId(recipe.getType());
            if (typeId == null) {
                throw new IllegalStateException("Unregistered recipe type");
            }
            return typeId;
        },
        typeId -> {
            RecipeType<?> type = Registries.RECIPE_TYPES.get(typeId);
            if (type == null) {
                return Codec.error("Unknown recipe type: " + typeId);
            }
            return type.codec().unchecked();
        }
    ).describe("CustomRecipe");

    /**
     * @return The unique namespaced identifier representing this recipe instance.
     */
    @NotNull Key getKey();

    /**
     * Determines whether this custom recipe should silently override and replace
     * a pre-existing recipe with the same key.
     *
     * @return True if the recipe acts as a replacement, false otherwise.
     */
    default boolean replace() {
        return false;
    }

    /**
     * Retrieves the structural type format associated with this recipe variant.
     *
     * @return The bound {@link RecipeType}.
     */
    RecipeType<?> getType();
}