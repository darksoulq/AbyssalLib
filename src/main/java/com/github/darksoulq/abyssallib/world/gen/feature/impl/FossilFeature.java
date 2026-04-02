package com.github.darksoulq.abyssallib.world.gen.feature.impl;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.world.gen.feature.Feature;
import com.github.darksoulq.abyssallib.world.gen.feature.FeatureConfig;
import com.github.darksoulq.abyssallib.world.gen.feature.FeaturePlaceContext;
import com.github.darksoulq.abyssallib.world.gen.feature.GenerationPhase;
import com.github.darksoulq.abyssallib.world.gen.internal.WorldGenUtils;
import com.github.darksoulq.abyssallib.world.gen.state.provider.BlockStateProvider;
import org.bukkit.Location;
import org.bukkit.util.noise.SimplexNoiseGenerator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A world generation feature that generates a contiguous, noise-driven blob of blocks.
 * <p>
 * Evaluates 3D Simplex Noise within a spherical bounding box to carve out solid,
 * randomized organic structures like ancient fossils, meteorite chunks, or massive
 * corrupted root systems.
 */
public class FossilFeature extends Feature<FossilFeature.Config> {

    /**
     * Constructs a new FossilFeature with its associated configuration codec.
     */
    public FossilFeature() {
        super(Config.CODEC);
    }

    /**
     * Executes the placement logic by evaluating 3D noise across the defined spherical volume.
     *
     * @param context The feature place context providing world access and configuration.
     * @return True if at least one block was successfully placed.
     */
    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        Location origin = context.origin();
        Config config = context.config();
        SimplexNoiseGenerator noiseGen = new SimplexNoiseGenerator(context.level().getWorld());
        
        int placedCount = 0;
        int radius = config.radius();
        double radiusSq = radius * radius;
        double frequency = config.noiseFrequency();
        double threshold = config.noiseThreshold();

        int minHeight = context.level().getWorld().getMinHeight();
        int maxHeight = context.level().getWorld().getMaxHeight();

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    
                    if (x * x + y * y + z * z > radiusSq) {
                        continue;
                    }

                    Location target = origin.clone().add(x, y, z);

                    if (target.getBlockY() < minHeight || target.getBlockY() >= maxHeight) {
                        continue;
                    }

                    double noise = noiseGen.noise(
                            target.getBlockX() * frequency,
                            target.getBlockY() * frequency,
                            target.getBlockZ() * frequency
                    );

                    if (noise > threshold) {
                        if (WorldGenUtils.isValidBlock(context.level(), target, config.targets())) {
                            BlockInfo stateToPlace = config.stateProvider().getState(context.random(), target);
                            if (stateToPlace != null) {
                                WorldGenUtils.placeBlock(context.level(), target, stateToPlace);
                                placedCount++;
                            }
                        }
                    }
                }
            }
        }

        return placedCount > 0;
    }

    /**
     * Specifies the procedural generation phase in which this feature executes.
     *
     * @return The UNDERGROUND_STRUCTURES generation phase.
     */
    @Override
    public GenerationPhase getPhase(Config config) {
        return GenerationPhase.UNDERGROUND_STRUCTURES;
    }

    /**
     * Configuration record for the fossil feature.
     *
     * @param stateProvider  The dynamic provider supplying the block used to build the fossil.
     * @param targets        The list of allowed target block identifiers that can be overwritten.
     * @param radius         The bounding radius for the 3D noise evaluation.
     * @param noiseFrequency The multiplier applied to coordinates before sampling the noise field.
     * @param noiseThreshold The breakpoint value that the 3D noise must exceed to place a block.
     */
    public record Config(
            BlockStateProvider stateProvider,
            List<BlockInfo> targets,
            int radius,
            double noiseFrequency,
            double noiseThreshold
    ) implements FeatureConfig {

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
             * @throws CodecException If required fields are missing.
             */
            @Override
            public <D> Config decode(DynamicOps<D> ops, D input) throws CodecException {
                Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
                
                BlockStateProvider stateProvider = BlockStateProvider.CODEC.decode(ops, map.get(ops.createString("state_provider")));
                List<BlockInfo> targets = ExtraCodecs.BLOCK_INFO.list().decode(ops, map.get(ops.createString("targets")));
                int radius = Codecs.INT.decode(ops, map.get(ops.createString("radius")));
                double noiseFrequency = Codecs.DOUBLE.decode(ops, map.get(ops.createString("noise_frequency")));
                double noiseThreshold = Codecs.DOUBLE.decode(ops, map.get(ops.createString("noise_threshold")));
                
                return new Config(stateProvider, targets, radius, noiseFrequency, noiseThreshold);
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
                
                map.put(ops.createString("state_provider"), BlockStateProvider.CODEC.encode(ops, value.stateProvider));
                map.put(ops.createString("targets"), ExtraCodecs.BLOCK_INFO.list().encode(ops, value.targets));
                map.put(ops.createString("radius"), Codecs.INT.encode(ops, value.radius));
                map.put(ops.createString("noise_frequency"), Codecs.DOUBLE.encode(ops, value.noiseFrequency));
                map.put(ops.createString("noise_threshold"), Codecs.DOUBLE.encode(ops, value.noiseThreshold));
                
                return ops.createMap(map);
            }
        };
    }
}