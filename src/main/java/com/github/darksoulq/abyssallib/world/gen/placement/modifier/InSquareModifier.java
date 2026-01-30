package com.github.darksoulq.abyssallib.world.gen.placement.modifier;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementContext;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifier;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifierType;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.stream.Stream;

/**
 * A placement modifier that randomizes the horizontal coordinates within a chunk.
 * <p>
 * This modifier shifts the X and Z coordinates of each input position by a random
 * value between 0 and 15. This is the standard method for distributing features
 * uniformly across the surface or underground of a single chunk.
 */
public class InSquareModifier extends PlacementModifier {

    /** Singleton instance of the modifier, as it carries no unique state.
     */
    private static final InSquareModifier INSTANCE = new InSquareModifier();

    /**
     * The codec used for serializing and deserializing the square placement modifier.
     * <p>
     * Since the modifier is stateless, the codec always returns the singleton
     * instance and encodes to an empty map.
     */
    public static final Codec<InSquareModifier> CODEC = new Codec<>() {
        /**
         * Returns the singleton instance of the modifier.
         *
         * @param ops   The dynamic operations logic.
         * @param input The serialized input.
         * @param <D>   The data format type.
         * @return The {@link InSquareModifier#INSTANCE}.
         */
        @Override
        public <D> InSquareModifier decode(DynamicOps<D> ops, D input) { return INSTANCE; }

        /**
         * Encodes the modifier into an empty map representation.
         *
         * @param ops   The dynamic operations logic.
         * @param value The modifier instance.
         * @param <D>   The data format type.
         * @return An empty map.
         */
        @Override
        public <D> D encode(DynamicOps<D> ops, InSquareModifier value) { return ops.createMap(Collections.emptyMap()); }
    };

    /**
     * The registered type definition for the square placement modifier.
     */
    public static final PlacementModifierType<InSquareModifier> TYPE = () -> CODEC;

    /**
     * Retrieves the singleton instance of this modifier.
     *
     * @return The {@link InSquareModifier} instance.
     */
    public static InSquareModifier instance() { return INSTANCE; }

    /**
     * Randomizes the X and Z coordinates of each input position within a chunk.
     * <p>
     * For every incoming vector, a random offset [0, 15] is added to the
     * existing X and Z coordinates. The Y-coordinate remains unchanged.
     *
     * @param context   The current {@link PlacementContext}.
     * @param positions The incoming stream of potential placement vectors.
     * @return A stream of vectors distributed within the horizontal bounds of the chunk.
     */
    @Override
    public Stream<Vector> getPositions(PlacementContext context, Stream<Vector> positions) {
        return positions.map(pos -> {
            int x = context.random().nextInt(16) + pos.getBlockX();
            int z = context.random().nextInt(16) + pos.getBlockZ();
            return new Vector(x, pos.getY(), z);
        });
    }

    /**
     * Retrieves the specific type definition for this modifier.
     *
     * @return The {@link PlacementModifierType} associated with {@link InSquareModifier}.
     */
    @Override
    public PlacementModifierType<?> getType() {
        return TYPE;
    }
}