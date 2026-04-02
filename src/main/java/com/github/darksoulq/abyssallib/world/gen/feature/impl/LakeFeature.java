package com.github.darksoulq.abyssallib.world.gen.feature.impl;

import com.github.darksoulq.abyssallib.common.serialization.BlockInfo;
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.gen.feature.Feature;
import com.github.darksoulq.abyssallib.world.gen.feature.FeatureConfig;
import com.github.darksoulq.abyssallib.world.gen.feature.FeaturePlaceContext;
import com.github.darksoulq.abyssallib.world.gen.feature.GenerationPhase;
import com.github.darksoulq.abyssallib.world.gen.internal.WorldGenUtils;
import com.github.darksoulq.abyssallib.world.gen.state.provider.BlockStateProvider;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * A world generation feature that generates a randomized lake or underground pool.
 */
public class LakeFeature extends Feature<LakeFeature.Config> {

    /**
     * Constructs a new LakeFeature with its associated configuration codec.
     */
    public LakeFeature() {
        super(Config.CODEC);
    }

    /**
     * Executes the generation of the lake cavity and fluid placement.
     *
     * @param context The feature place context providing world access and configuration.
     * @return True if the lake was successfully generated.
     */
    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        Location origin = context.origin();
        Random random = context.random();
        Config config = context.config();

        if (origin.getBlockY() <= context.level().getWorld().getMinHeight() + 4) {
            return false;
        }

        boolean[] isLakeCavity = new boolean[2048];
        int nodeCount = random.nextInt(4) + 4;

        for (int node = 0; node < nodeCount; ++node) {
            double xNodeBounds = random.nextDouble() * 6.0D + 3.0D;
            double yNodeBounds = random.nextDouble() * 4.0D + 2.0D;
            double zNodeBounds = random.nextDouble() * 6.0D + 3.0D;
            double xCenter = random.nextDouble() * (16.0D - xNodeBounds - 2.0D) + 1.0D + xNodeBounds / 2.0D;
            double yCenter = random.nextDouble() * (8.0D - yNodeBounds - 4.0D) + 2.0D + yNodeBounds / 2.0D;
            double zCenter = random.nextDouble() * (16.0D - zNodeBounds - 2.0D) + 1.0D + zNodeBounds / 2.0D;

            for (int gridX = 1; gridX < 15; ++gridX) {
                for (int gridZ = 1; gridZ < 15; ++gridZ) {
                    for (int gridY = 1; gridY < 7; ++gridY) {
                        double dx = ((double) gridX - xCenter) / (xNodeBounds / 2.0D);
                        double dy = ((double) gridY - yCenter) / (yNodeBounds / 2.0D);
                        double dz = ((double) gridZ - zCenter) / (zNodeBounds / 2.0D);
                        double distanceSq = dx * dx + dy * dy + dz * dz;

                        if (distanceSq < 1.0D) {
                            isLakeCavity[(gridX * 16 + gridZ) * 8 + gridY] = true;
                        }
                    }
                }
            }
        }

        for (int gridX = 0; gridX < 16; ++gridX) {
            for (int gridZ = 0; gridZ < 16; ++gridZ) {
                for (int gridY = 0; gridY < 8; ++gridY) {
                    if (isLakeCavity[(gridX * 16 + gridZ) * 8 + gridY]) {
                        Location targetLoc = origin.clone().add(gridX, gridY - 4, gridZ);
                        
                        if (gridY < 4) {
                            BlockInfo fluidState = config.fluidProvider().getState(random, targetLoc);
                            if (fluidState != null) {
                                WorldGenUtils.placeBlock(context.level(), targetLoc, fluidState);
                            }
                        } else {
                            context.level().setBlock(targetLoc.getBlockX(), targetLoc.getBlockY(), targetLoc.getBlockZ(), Material.AIR);
                        }

                        if (config.barrierProvider() != null) {
                            boolean isBoundary = !isLakeCavity[((gridX - 1) * 16 + gridZ) * 8 + gridY] ||
                                                 !isLakeCavity[((gridX + 1) * 16 + gridZ) * 8 + gridY] ||
                                                 !isLakeCavity[(gridX * 16 + (gridZ - 1)) * 8 + gridY] ||
                                                 !isLakeCavity[(gridX * 16 + (gridZ + 1)) * 8 + gridY] ||
                                                 !isLakeCavity[(gridX * 16 + gridZ) * 8 + (gridY - 1)] ||
                                                 !isLakeCavity[(gridX * 16 + gridZ) * 8 + (gridY + 1)];

                            if (isBoundary && gridY < 4) {
                                BlockInfo barrierState = config.barrierProvider().getState(random, targetLoc);
                                if (barrierState != null) {
                                    WorldGenUtils.placeBlock(context.level(), targetLoc, barrierState);
                                }
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    /**
     * Specifies the procedural generation phase in which this feature executes.
     *
     * @return The LAKES generation phase.
     */
    @Override
    public GenerationPhase getPhase(Config config) {
        return GenerationPhase.LAKES;
    }

    /**
     * Configuration record for the lake feature.
     *
     * @param fluidProvider   The block state provider defining the core liquid of the lake.
     * @param barrierProvider The optional block state provider defining the outer casing of the lake.
     */
    public record Config(BlockStateProvider fluidProvider, BlockStateProvider barrierProvider) implements FeatureConfig {

        /**
         * The codec for serializing and deserializing the configuration.
         */
        public static final Codec<Config> CODEC = new Codec<>() {

            /**
             * Decodes the configuration from a map.
             *
             * @param ops   The dynamic operations logic.
             * @param input The serialized input.
             * @param <D>   The data format type.
             * @return A new configuration instance.
             * @throws CodecException If the fluid provider field is missing.
             */
            @Override
            public <D> Config decode(DynamicOps<D> ops, D input) throws CodecException {
                Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
                BlockStateProvider fluid = BlockStateProvider.CODEC.decode(ops, map.get(ops.createString("fluid_provider")));
                
                BlockStateProvider barrier = null;
                D barrierNode = map.get(ops.createString("barrier_provider"));
                if (barrierNode != null) {
                    barrier = BlockStateProvider.CODEC.decode(ops, barrierNode);
                }
                
                return new Config(fluid, barrier);
            }

            /**
             * Encodes the configuration into a map.
             *
             * @param ops   The dynamic operations logic.
             * @param value The configuration instance.
             * @param <D>   The data format type.
             * @return The encoded data object.
             * @throws CodecException If serialization fails.
             */
            @Override
            public <D> D encode(DynamicOps<D> ops, Config value) throws CodecException {
                Map<D, D> map = new HashMap<>();
                map.put(ops.createString("fluid_provider"), BlockStateProvider.CODEC.encode(ops, value.fluidProvider));
                
                if (value.barrierProvider != null) {
                    map.put(ops.createString("barrier_provider"), BlockStateProvider.CODEC.encode(ops, value.barrierProvider));
                }
                
                return ops.createMap(map);
            }
        };
    }
}