package com.github.darksoulq.abyssallib.world.gen.placement.modifier;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementContext;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifier;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifierType;
import org.bukkit.HeightMap;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * A placement modifier that filters positions based on their vertical proximity
 * to a selected world heightmap (surface).
 * <p>
 * If the incoming vector's Y-coordinate does not fall within the configured
 * relative bounds, the vector is discarded.
 */
public class SurfaceRelativeThresholdModifier extends PlacementModifier {

    /**
     * The codec used for serializing and deserializing the surface relative threshold modifier.
     */
    public static final Codec<SurfaceRelativeThresholdModifier> CODEC = new Codec<>() {

        /**
         * Decodes the modifier from a serialized map.
         *
         * @param ops   The dynamic operations logic.
         * @param input The serialized input.
         * @param <D>   The data format type.
         * @return A new instance of the surface relative threshold modifier.
         * @throws CodecException If the required fields are missing.
         */
        @Override
        public <D> SurfaceRelativeThresholdModifier decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            HeightMap heightmap = Codec.enumCodec(HeightMap.class).decode(ops, map.get(ops.createString("heightmap")));
            int minInclusive = Codecs.INT.decode(ops, map.get(ops.createString("min_inclusive")));
            int maxInclusive = Codecs.INT.decode(ops, map.get(ops.createString("max_inclusive")));
            return new SurfaceRelativeThresholdModifier(heightmap, minInclusive, maxInclusive);
        }

        /**
         * Encodes the modifier into a serialized map.
         *
         * @param ops   The dynamic operations logic.
         * @param value The modifier instance to encode.
         * @param <D>   The data format type.
         * @return The encoded data object.
         * @throws CodecException If serialization fails.
         */
        @Override
        public <D> D encode(DynamicOps<D> ops, SurfaceRelativeThresholdModifier value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("heightmap"), Codec.enumCodec(HeightMap.class).encode(ops, value.heightmap));
            map.put(ops.createString("min_inclusive"), Codecs.INT.encode(ops, value.minInclusive));
            map.put(ops.createString("max_inclusive"), Codecs.INT.encode(ops, value.maxInclusive));
            return ops.createMap(map);
        }
    };

    /**
     * The registered type definition for the surface relative threshold placement modifier.
     */
    public static final PlacementModifierType<SurfaceRelativeThresholdModifier> TYPE = () -> CODEC;

    /** The heightmap criteria used to locate the surface block. */
    private final HeightMap heightmap;

    /** The minimum allowed relative offset from the surface (inclusive). */
    private final int minInclusive;

    /** The maximum allowed relative offset from the surface (inclusive). */
    private final int maxInclusive;

    /**
     * Constructs a new SurfaceRelativeThresholdModifier.
     *
     * @param heightmap    The target surface heightmap (e.g., OCEAN_FLOOR, WORLD_SURFACE).
     * @param minInclusive The lowest relative offset allowed.
     * @param maxInclusive The highest relative offset allowed.
     */
    public SurfaceRelativeThresholdModifier(HeightMap heightmap, int minInclusive, int maxInclusive) {
        this.heightmap = heightmap;
        this.minInclusive = minInclusive;
        this.maxInclusive = maxInclusive;
    }

    /**
     * Filters the incoming positions by checking if their absolute Y-coordinate falls within
     * the allowed bounds relative to the surface Y-coordinate at their X/Z location.
     *
     * @param context   The current placement context.
     * @param positions The incoming stream of potential placement vectors.
     * @return A filtered stream of vectors that met the surface proximity condition.
     */
    @Override
    public Stream<Vector> getPositions(PlacementContext context, Stream<Vector> positions) {
        return positions.filter(pos -> {
            int surfaceY = context.level().getHighestBlockY(pos.getBlockX(), pos.getBlockZ(), heightmap);
            int minAllowedY = surfaceY + minInclusive;
            int maxAllowedY = surfaceY + maxInclusive;

            return pos.getBlockY() >= minAllowedY && pos.getBlockY() <= maxAllowedY;
        });
    }

    /**
     * Retrieves the specific type definition for this modifier.
     *
     * @return The placement modifier type associated with this surface relative threshold modifier.
     */
    @Override
    public PlacementModifierType<?> getType() {
        return TYPE;
    }
}