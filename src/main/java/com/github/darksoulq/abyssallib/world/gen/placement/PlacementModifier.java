package com.github.darksoulq.abyssallib.world.gen.placement;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import org.bukkit.util.Vector;

import java.util.stream.Stream;

/**
 * The base class for all placement logic transformations in the world generation pipeline.
 */
public abstract class PlacementModifier {

    /**
     * Polymorphic codec for serializing and deserializing any placement modifier implementation.
     */
    public static final Codec<PlacementModifier> CODEC = Codec.dispatch(
        PlacementModifier.class,
        "type",
        Codecs.STRING,
        modifier -> {
            String typeId = Registries.PLACEMENT_MODIFIERS.getId(modifier.getType());
            if (typeId == null) {
                throw new IllegalStateException("Unregistered placement modifier type");
            }
            return typeId;
        },
        typeId -> {
            PlacementModifierType<?> type = Registries.PLACEMENT_MODIFIERS.get(typeId);
            if (type == null) {
                return Codec.error("Unknown placement modifier type: " + typeId);
            }
            return type.codec().unchecked();
        }
    ).describe("PlacementModifier");

    /**
     * Transforms the stream of positions according to the specific modifier logic.
     *
     * @param context   The current placement context providing world and environmental data.
     * @param positions The input stream of potential placement vector coordinates.
     * @return A modified stream of vectors after application of the modifier logic.
     */
    public abstract Stream<Vector> getPositions(PlacementContext context, Stream<Vector> positions);

    /**
     * Retrieves the placement modifier type used for identifying this specific implementation.
     *
     * @return The placement modifier type associated with this instance.
     */
    public abstract PlacementModifierType<?> getType();
}