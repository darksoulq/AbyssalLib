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
 * A placement modifier that assigns a random vertical coordinate within a fixed range.
 * <p>
 * This modifier transforms the Y-coordinate of each position in the stream by
 * selecting a random integer between the configured minimum and maximum bounds.
 * It is commonly used for features that generate at specific subterranean
 * or atmospheric levels.
 */
public class HeightRangeModifier extends PlacementModifier {

    /**
     * The codec used for serializing and deserializing the height range modifier.
     * <p>
     * It requires "min" and "max" integer fields to define the vertical boundaries.
     */
    public static final Codec<HeightRangeModifier> CODEC = new Codec<>() {
        /**
         * Decodes a HeightRangeModifier from the provided serialized data.
         *
         * @param ops   The dynamic operations logic.
         * @param input The serialized input data.
         * @param <D>   The data format type.
         * @return A new instance of {@link HeightRangeModifier}.
         * @throws CodecException If the "min" or "max" fields are missing or invalid.
         */
        @Override
        public <D> HeightRangeModifier decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            int min = Codecs.INT.decode(ops, map.get(ops.createString("min")));
            int max = Codecs.INT.decode(ops, map.get(ops.createString("max")));
            return new HeightRangeModifier(min, max);
        }

        /**
         * Encodes the height range modifier into a serialized map.
         *
         * @param ops   The dynamic operations logic.
         * @param value The modifier instance to encode.
         * @param <D>   The data format type.
         * @return A map containing the min and max height values.
         * @throws CodecException If serialization fails.
         */
        @Override
        public <D> D encode(DynamicOps<D> ops, HeightRangeModifier value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("min"), Codecs.INT.encode(ops, value.min));
            map.put(ops.createString("max"), Codecs.INT.encode(ops, value.max));
            return ops.createMap(map);
        }
    };

    /**
     * The registered type definition for the height range placement modifier.
     */
    public static final PlacementModifierType<HeightRangeModifier> TYPE = () -> CODEC;

    /** The minimum Y-coordinate (inclusive). */
    private final int min;

    /** The maximum Y-coordinate (inclusive). */
    private final int max;

    /**
     * Constructs a new HeightRangeModifier.
     *
     * @param min The lower bound of the vertical range.
     * @param max The upper bound of the vertical range.
     */
    public HeightRangeModifier(int min, int max) {
        this.min = min;
        this.max = max;
    }

    /**
     * Maps each position in the stream to a random height within the configured range.
     * <p>
     * For every incoming vector, the X and Z coordinates are preserved, while the
     * Y coordinate is recalculated using the random source provided by the
     * {@link PlacementContext}.
     *
     * @param context   The current {@link PlacementContext}.
     * @param positions The incoming stream of potential placement vectors.
     * @return A stream of vectors with randomized vertical coordinates.
     */
    @Override
    public Stream<Vector> getPositions(PlacementContext context, Stream<Vector> positions) {
        return positions.map(pos -> {
            int y = context.random().nextInt(max - min + 1) + min;
            return new Vector(pos.getX(), y, pos.getZ());
        });
    }

    /**
     * Retrieves the specific type definition for this modifier.
     *
     * @return The {@link PlacementModifierType} associated with {@link HeightRangeModifier}.
     */
    @Override
    public PlacementModifierType<?> getType() {
        return TYPE;
    }
}