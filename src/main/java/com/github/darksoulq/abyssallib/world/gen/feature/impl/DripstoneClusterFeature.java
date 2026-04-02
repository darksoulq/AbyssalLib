package com.github.darksoulq.abyssallib.world.gen.feature.impl;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.world.gen.feature.Feature;
import com.github.darksoulq.abyssallib.world.gen.feature.FeatureConfig;
import com.github.darksoulq.abyssallib.world.gen.feature.FeaturePlaceContext;
import com.github.darksoulq.abyssallib.world.gen.feature.GenerationPhase;
import com.github.darksoulq.abyssallib.world.gen.internal.WorldGenUtils;
import com.github.darksoulq.abyssallib.world.gen.state.provider.BlockStateProvider;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * A world generation feature that creates varied clusters of vertical pillars, 
 * simulating stalactites and stalagmites.
 * <p>
 * This feature randomly places multiple pillars within a defined horizontal radius.
 * The length of each pillar decreases naturally as it approaches the outer edges
 * of the radius, creating an organic cluster shape.
 */
public class DripstoneClusterFeature extends Feature<DripstoneClusterFeature.Config> {

    /**
     * Constructs a new DripstoneClusterFeature with its associated configuration codec.
     */
    public DripstoneClusterFeature() {
        super(Config.CODEC);
    }

    /**
     * Executes the logic for clustering varying-length pillars around the origin.
     *
     * @param context The feature place context providing world access and configuration.
     * @return True if at least one column within the cluster was successfully placed.
     */
    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        Location origin = context.origin();
        Config config = context.config();
        Random random = context.random();
        int placedCount = 0;

        int radius = config.radius();
        int maxHeight = config.maxHeight();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                double distanceSq = dx * dx + dz * dz;
                if (distanceSq > radius * radius) {
                    continue;
                }

                if (random.nextDouble() > 0.7) {
                    continue;
                }

                double edgeFactor = 1.0 - (Math.sqrt(distanceSq) / radius);
                int localHeight = Math.max(1, (int) (maxHeight * edgeFactor * (0.5 + random.nextDouble() * 0.5)));

                Location currentOrigin = origin.clone().add(dx, 0, dz);
                boolean placedColumn = false;

                for (int y = 0; y < localHeight; y++) {
                    Location target = currentOrigin.clone().add(0, config.upward() ? y : -y, 0);

                    if (target.getBlockY() < context.level().getWorld().getMinHeight() || 
                        target.getBlockY() >= context.level().getWorld().getMaxHeight()) {
                        break;
                    }

                    if (WorldGenUtils.isValidBlock(context.level(), target, config.targets())) {
                        BlockInfo stateToPlace = config.stateProvider().getState(random, target);
                        if (stateToPlace != null) {
                            WorldGenUtils.placeBlock(context.level(), target, stateToPlace);
                            placedColumn = true;
                        }
                    } else {
                        break;
                    }
                }

                if (placedColumn) {
                    placedCount++;
                }
            }
        }

        return placedCount > 0;
    }

    /**
     * Specifies the procedural generation phase in which this feature executes.
     *
     * @return The UNDERGROUND_DECORATION generation phase.
     */
    @Override
    public GenerationPhase getPhase(Config config) {
        return GenerationPhase.UNDERGROUND_DECORATION;
    }

    /**
     * Configuration record for the dripstone cluster feature.
     *
     * @param stateProvider The dynamic provider supplying the block used to build the cluster.
     * @param targets       The list of allowed target block identifiers that can be overwritten.
     * @param radius        The maximum horizontal radius of the cluster.
     * @param maxHeight     The maximum possible block length for the central columns.
     * @param upward        True to generate the cluster growing upwards, false for downwards.
     */
    public record Config(
            BlockStateProvider stateProvider,
            List<BlockInfo> targets,
            int radius,
            int maxHeight,
            boolean upward
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
                int maxHeight = Codecs.INT.decode(ops, map.get(ops.createString("max_height")));
                boolean upward = Codecs.BOOLEAN.decode(ops, map.get(ops.createString("upward")));
                
                return new Config(stateProvider, targets, radius, maxHeight, upward);
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
                map.put(ops.createString("max_height"), Codecs.INT.encode(ops, value.maxHeight));
                map.put(ops.createString("upward"), Codecs.BOOLEAN.encode(ops, value.upward));
                
                return ops.createMap(map);
            }
        };
    }
}