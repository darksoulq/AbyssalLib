package com.github.darksoulq.abyssallib.world.gen.placement.modifier;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementContext;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifier;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifierType;
import org.bukkit.util.Vector;

import java.util.stream.Stream;

/**
 * A placement modifier that spreads the input positions randomly across the X and Z axes
 * within the bounds of a standard 16x16 chunk.
 */
public class InSquareModifier extends PlacementModifier {

    /**
     * The codec used for serializing and deserializing the in-square modifier.
     */
    public static final Codec<InSquareModifier> CODEC = new Codec<>() {
        @Override
        public <D> InSquareModifier decode(DynamicOps<D> ops, D input) {
            return new InSquareModifier();
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, InSquareModifier value) {
            return ops.createMap(new java.util.HashMap<>());
        }
    };

    /**
     * The registered type definition for the in-square placement modifier.
     */
    public static final PlacementModifierType<InSquareModifier> TYPE = () -> CODEC;

    /**
     * Constructs a new InSquareModifier.
     */
    public InSquareModifier() {}

    /**
     * Applies a random horizontal offset to each incoming position.
     * The offset is between 0 and 15 blocks on both the X and Z axes.
     *
     * @param context   The current placement context.
     * @param positions The incoming stream of potential placement vectors.
     * @return A stream of vectors randomly offset horizontally.
     */
    @Override
    public Stream<Vector> getPositions(PlacementContext context, Stream<Vector> positions) {
        return positions.map(pos -> {
            int xOffset = context.random().nextInt(16);
            int zOffset = context.random().nextInt(16);
            return new Vector(pos.getBlockX() + xOffset, pos.getBlockY(), pos.getBlockZ() + zOffset);
        });
    }

    @Override
    public PlacementModifierType<?> getType() {
        return TYPE;
    }
}