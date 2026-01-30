package com.github.darksoulq.abyssallib.world.gen.placement.modifier;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementContext;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifier;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifierType;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * A placement modifier that performs a vertical scan to find the nearest solid surface.
 * <p>
 * This modifier iterates vertically from the input position, searching for a solid block.
 * Depending on the configuration, it can scan upwards to find a ceiling or downwards
 * to find a floor, adjusting the final vector to sit on or under that surface.
 */
public class EnvironmentScanModifier extends PlacementModifier {

    /**
     * The codec used for serializing and deserializing the environment scan modifier.
     */
    public static final Codec<EnvironmentScanModifier> CODEC = new Codec<>() {
        /**
         * Decodes the modifier from a serialized map.
         *
         * @param ops   The dynamic operations logic.
         * @param input The serialized input.
         * @param <D>   The data format type.
         * @return A new instance of {@link EnvironmentScanModifier}.
         * @throws CodecException If "max_steps" or "up" fields are missing or invalid.
         */
        @Override
        public <D> EnvironmentScanModifier decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            int max = Codecs.INT.decode(ops, map.get(ops.createString("max_steps")));
            boolean up = Codecs.BOOLEAN.decode(ops, map.get(ops.createString("up")));
            return new EnvironmentScanModifier(max, up);
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
        public <D> D encode(DynamicOps<D> ops, EnvironmentScanModifier value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("max_steps"), Codecs.INT.encode(ops, value.maxSteps));
            map.put(ops.createString("up"), Codecs.BOOLEAN.encode(ops, value.up));
            return ops.createMap(map);
        }
    };

    /**
     * The registered type definition for the environment scan placement modifier.
     */
    public static final PlacementModifierType<EnvironmentScanModifier> TYPE = () -> CODEC;

    /** The maximum number of blocks to scan in the specified direction. */
    private final int maxSteps;

    /** Whether to scan upwards (true) or downwards (false). */
    private final boolean up;

    /**
     * Constructs a new EnvironmentScanModifier.
     *
     * @param maxSteps The range of the vertical scan.
     * @param up       True to find a ceiling, false to find a floor.
     */
    public EnvironmentScanModifier(int maxSteps, boolean up) {
        this.maxSteps = maxSteps;
        this.up = up;
    }

    /**
     * Maps each input position to the nearest solid surface found within the scan range.
     * <p>
     * The method iterates through the Y-axis starting at the input coordinate. If a
     * solid block is encountered within the {@code maxSteps} range, the position is
     * updated. If the scan is upward, it returns the position of the air block
     * immediately above the surface; if downward, it returns the surface position itself.
     *
     * @param context   The current {@link PlacementContext}.
     * @param positions The incoming stream of potential placement vectors.
     * @return A stream of vectors adjusted to the discovered surfaces.
     */
    @Override
    public Stream<Vector> getPositions(PlacementContext context, Stream<Vector> positions) {
        return positions.map(pos -> {
            int x = pos.getBlockX();
            int y = pos.getBlockY();
            int z = pos.getBlockZ();
            int step = up ? 1 : -1;

            for (int i = 0; i < maxSteps; i++) {
                int checkY = y + (i * step);
                if (checkY < context.getMinBuildHeight() || checkY >= context.getHeight()) break;

                Material m = context.level().getType(x, checkY, z);
                if (m.isSolid()) {
                    return new Vector(x, checkY + (up ? 1 : 0), z);
                }
            }
            return pos;
        });
    }

    /**
     * Retrieves the specific type definition for this modifier.
     *
     * @return The {@link PlacementModifierType} associated with {@link EnvironmentScanModifier}.
     */
    @Override
    public PlacementModifierType<?> getType() {
        return TYPE;
    }
}