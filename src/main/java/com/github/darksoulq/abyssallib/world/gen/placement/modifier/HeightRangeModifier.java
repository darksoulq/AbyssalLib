package com.github.darksoulq.abyssallib.world.gen.placement.modifier;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.RecordBuilder;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementContext;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifier;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifierType;
import org.bukkit.util.Vector;

import java.util.stream.Stream;

/**
 * A placement modifier that randomizes the Y-coordinate of input positions
 * between a specified minimum and maximum bounds.
 */
public class HeightRangeModifier extends PlacementModifier {

    /**
     * The codec used for serializing and deserializing the height range modifier.
     */
    public static final Codec<HeightRangeModifier> CODEC = RecordBuilder.create(instance -> instance.group(
        Codecs.INT.fieldOf("min_inclusive").forGetter(HeightRangeModifier.class, p -> p.minInclusive),
        Codecs.INT.fieldOf("max_inclusive").forGetter(HeightRangeModifier.class, p -> p.maxInclusive)
    ).apply(instance, HeightRangeModifier::new)).describe("HeightRangeModifier");

    /**
     * The registered type definition for the height range placement modifier.
     */
    public static final PlacementModifierType<HeightRangeModifier> TYPE = () -> CODEC;

    /** The minimum allowed Y coordinate (inclusive). */
    private final int minInclusive;

    /** The maximum allowed Y coordinate (inclusive). */
    private final int maxInclusive;

    /**
     * Constructs a new HeightRangeModifier.
     *
     * @param minInclusive The lowest Y level to place features.
     * @param maxInclusive The highest Y level to place features.
     */
    public HeightRangeModifier(int minInclusive, int maxInclusive) {
        this.minInclusive = minInclusive;
        this.maxInclusive = maxInclusive;
    }

    /**
     * Replaces the Y coordinate of each input position with a random integer
     * between the configured minimum and maximum bounds.
     *
     * @param context   The current placement context.
     * @param positions The incoming stream of potential placement vectors.
     * @return A stream of vectors with randomized vertical positions.
     */
    @Override
    public Stream<Vector> getPositions(PlacementContext context, Stream<Vector> positions) {
        return positions.map(pos -> {
            int yRange = Math.max(1, (maxInclusive - minInclusive) + 1);
            int randomY = minInclusive + context.random().nextInt(yRange);
            return new Vector(pos.getBlockX(), randomY, pos.getBlockZ());
        });
    }

    @Override
    public PlacementModifierType<?> getType() {
        return TYPE;
    }
}