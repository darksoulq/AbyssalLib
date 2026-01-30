package com.github.darksoulq.abyssallib.world.gen.feature.impl;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.gen.feature.Feature;
import com.github.darksoulq.abyssallib.world.gen.feature.FeatureConfig;
import com.github.darksoulq.abyssallib.world.gen.feature.FeaturePlaceContext;
import com.github.darksoulq.abyssallib.world.gen.feature.util.BlockStateCodec;
import com.github.darksoulq.abyssallib.world.gen.internal.WorldGenUtils;
import com.github.darksoulq.abyssallib.world.structure.processor.BlockInfo;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A world generation feature that generates a disk-shaped volume of blocks.
 * <p>
 * This feature is typically used to generate circular patches of materials like sand,
 * gravel, or clay in riverbeds or on the surface. It replaces specific target
 * blocks within a cylindrical radius and a vertical half-height range.
 */
public class DiskFeature extends Feature<DiskFeature.Config> {

    /**
     * Constructs a new DiskFeature with the associated configuration codec.
     */
    public DiskFeature() {
        super(Config.CODEC);
    }

    /**
     * Executes the placement logic for the disk feature.
     * <p>
     * The algorithm iterates through a cubic volume defined by the radius and half-height.
     * It uses the Euclidean distance formula (x² + z²) to constrain placement within a
     * circular disk. Blocks are only placed if the existing block at the location
     * matches the criteria defined in the configuration's target list.
     *
     * @param context The {@link FeaturePlaceContext} providing world access, origin, random source, and configuration.
     * @return {@code true} if at least one block was successfully placed; {@code false} otherwise.
     */
    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        boolean placed = false;
        int radius = context.config().radius;
        int ySize = context.config().halfHeight;
        Location origin = context.origin();

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z <= radius * radius) {
                    for (int y = -ySize; y <= ySize; y++) {
                        Location loc = origin.clone().add(x, y, z);
                        if (WorldGenUtils.isValidBlock(context.level(), loc, context.config().targets)) {
                            WorldGenUtils.placeBlock(context.level(), loc, context.config().toPlace);
                            placed = true;
                        }
                    }
                }
            }
        }
        return placed;
    }

    /**
     * Configuration record for {@link DiskFeature}.
     *
     * @param toPlace    The {@link BlockInfo} representing the block state to be generated.
     * @param radius     The horizontal radius of the disk.
     * @param halfHeight The vertical distance to extend above and below the origin (total height = 2h + 1).
     * @param targets    A {@link List} of block identifiers that are allowed to be replaced by this feature.
     */
    public record Config(BlockInfo toPlace, int radius, int halfHeight, List<String> targets) implements FeatureConfig {

        /**
         * The codec for serializing and deserializing {@link Config}.
         */
        public static final Codec<Config> CODEC = new Codec<>() {

            /**
             * Decodes the configuration from a serialized map.
             *
             * @param ops   The dynamic operations logic.
             * @param input The serialized input.
             * @param <D>   The data format type.
             * @return A new {@link Config} instance.
             * @throws CodecException If required fields are missing or invalid.
             */
            @Override
            public <D> Config decode(DynamicOps<D> ops, D input) throws CodecException {
                Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
                BlockInfo block = BlockStateCodec.CODEC.decode(ops, map.get(ops.createString("block")));
                int r = Codecs.INT.decode(ops, map.get(ops.createString("radius")));
                int h = Codecs.INT.decode(ops, map.get(ops.createString("half_height")));

                List<String> targets = new ArrayList<>();
                if (map.containsKey(ops.createString("targets"))) {
                    targets = Codecs.STRING.list().decode(ops, map.get(ops.createString("targets")));
                }
                return new Config(block, r, h, targets);
            }

            /**
             * Encodes the configuration into a serialized map.
             *
             * @param ops   The dynamic operations logic.
             * @param value The configuration instance to encode.
             * @param <D>   The data format type.
             * @return The encoded data object.
             * @throws CodecException If serialization fails.
             */
            @Override
            public <D> D encode(DynamicOps<D> ops, Config value) throws CodecException {
                Map<D, D> map = new HashMap<>();
                map.put(ops.createString("block"), BlockStateCodec.CODEC.encode(ops, value.toPlace));
                map.put(ops.createString("radius"), Codecs.INT.encode(ops, value.radius));
                map.put(ops.createString("half_height"), Codecs.INT.encode(ops, value.halfHeight));
                map.put(ops.createString("targets"), Codecs.STRING.list().encode(ops, value.targets));
                return ops.createMap(map);
            }
        };
    }
}