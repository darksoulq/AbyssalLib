package com.github.darksoulq.abyssallib.world.gen.placement.modifier;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementContext;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifier;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifierType;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

/**
 * A placement modifier that filters positions based on the depth of water above the terrain.
 * <p>
 * This modifier scans downward from the current Y-coordinate. If it encounters a solid
 * block before exceeding the maximum allowed water depth, the position is kept.
 * If the depth exceeds the limit or air is found, the position is filtered out.
 */
public class SurfaceWaterDepthFilter extends PlacementModifier {

    /**
     * The codec used for serializing and deserializing the surface water depth filter.
     */
    public static final Codec<SurfaceWaterDepthFilter> CODEC = new Codec<>() {
        /**
         * Decodes a SurfaceWaterDepthFilter from the provided serialized data.
         *
         * @param ops   The dynamic operations logic.
         * @param input The serialized input data.
         * @param <D>   The data format type.
         * @return A new instance of {@link SurfaceWaterDepthFilter}.
         * @throws CodecException If "max_water_depth" is missing or invalid.
         */
        @Override
        public <D> SurfaceWaterDepthFilter decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            int max = Codecs.INT.decode(ops, map.get(ops.createString("max_water_depth")));
            return new SurfaceWaterDepthFilter(max);
        }

        /**
         * Encodes the surface water depth filter into a serialized map.
         *
         * @param ops   The dynamic operations logic.
         * @param value The modifier instance to encode.
         * @param <D>   The data format type.
         * @return A map containing the max_water_depth value.
         * @throws CodecException If serialization fails.
         */
        @Override
        public <D> D encode(DynamicOps<D> ops, SurfaceWaterDepthFilter value) throws CodecException {
            Map<D, D> map = Collections.singletonMap(
                ops.createString("max_water_depth"),
                Codecs.INT.encode(ops, value.maxWaterDepth)
            );
            return ops.createMap(map);
        }
    };

    /**
     * The registered type definition for the surface water depth placement modifier.
     */
    public static final PlacementModifierType<SurfaceWaterDepthFilter> TYPE = () -> CODEC;

    /** The maximum number of water blocks allowed above the floor. */
    private final int maxWaterDepth;

    /**
     * Constructs a new SurfaceWaterDepthFilter.
     *
     * @param maxWaterDepth The maximum vertical depth of water allowed.
     */
    public SurfaceWaterDepthFilter(int maxWaterDepth) {
        this.maxWaterDepth = maxWaterDepth;
    }

    /**
     * Filters the input stream by checking the water depth at each coordinate.
     * <p>
     * For each position, the method iterates downward up to the max depth.
     * If it finds a non-water/non-air block within that range, the filter passes.
     * If it only finds water or encounters air, the position is removed from the stream.
     *
     * @param context   The current {@link PlacementContext}.
     * @param positions The incoming stream of potential placement vectors.
     * @return A filtered stream of vectors satisfying the water depth criteria.
     */
    @Override
    public Stream<Vector> getPositions(PlacementContext context, Stream<Vector> positions) {
        return positions.filter(pos -> {
            int x = pos.getBlockX();
            int z = pos.getBlockZ();
            int y = pos.getBlockY();

            for (int i = 0; i <= maxWaterDepth; i++) {
                Material mat = context.level().getType(x, y - i, z);
                if (mat == Material.AIR) return false;
                if (mat != Material.WATER) return true;
            }
            return false;
        });
    }

    /**
     * Retrieves the specific type definition for this modifier.
     *
     * @return The {@link PlacementModifierType} associated with {@link SurfaceWaterDepthFilter}.
     */
    @Override
    public PlacementModifierType<?> getType() {
        return TYPE;
    }
}