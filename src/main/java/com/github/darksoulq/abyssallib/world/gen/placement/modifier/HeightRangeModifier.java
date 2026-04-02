package com.github.darksoulq.abyssallib.world.gen.placement.modifier;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementContext;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifier;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifierType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * A placement modifier that randomizes the Y-coordinate of input positions
 * between a specified minimum and maximum bounds.
 */
public class HeightRangeModifier extends PlacementModifier {

    /**
     * The codec used for serializing and deserializing the height range modifier.
     */
    public static final Codec<HeightRangeModifier> CODEC = new Codec<>() {
        @Override
        public <D> HeightRangeModifier decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            int min = Codecs.INT.decode(ops, map.get(ops.createString("min_inclusive")));
            int max = Codecs.INT.decode(ops, map.get(ops.createString("max_inclusive")));
            return new HeightRangeModifier(min, max);
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, HeightRangeModifier value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("min_inclusive"), Codecs.INT.encode(ops, value.minInclusive));
            map.put(ops.createString("max_inclusive"), Codecs.INT.encode(ops, value.maxInclusive));
            return ops.createMap(map);
        }
    };

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