package com.github.darksoulq.abyssallib.world.gen.feature.impl;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.world.gen.feature.Feature;
import com.github.darksoulq.abyssallib.world.gen.feature.FeatureConfig;
import com.github.darksoulq.abyssallib.world.gen.feature.FeaturePlaceContext;
import com.github.darksoulq.abyssallib.world.gen.internal.WorldGenUtils;
import com.github.darksoulq.abyssallib.world.gen.state.provider.BlockStateProvider;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A world generation feature that generates a flat, circular disk dynamically.
 * <p>
 * Upgraded to utilize the Block State Provider API, enabling features like
 * mixed gravel and sand ocean floors.
 */
public class DiskFeature extends Feature<DiskFeature.Config> {

    /**
     * Constructs a new DiskFeature with its associated configuration codec.
     */
    public DiskFeature() {
        super(Config.CODEC);
    }

    /**
     * Executes the circular placement logic centered on the given origin.
     *
     * @param context The feature place context providing world access and configuration.
     * @return True if at least one block was successfully placed.
     */
    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        Location origin = context.origin();
        Config config = context.config();
        int placedCount = 0;

        int minHeight = context.level().getWorld().getMinHeight();
        int maxHeight = context.level().getWorld().getMaxHeight();

        int radius = config.radius();
        int halfHeight = config.halfHeight();
        int radiusSq = radius * radius;

        for (int x = origin.getBlockX() - radius; x <= origin.getBlockX() + radius; x++) {
            for (int z = origin.getBlockZ() - radius; z <= origin.getBlockZ() + radius; z++) {
                int dx = x - origin.getBlockX();
                int dz = z - origin.getBlockZ();

                if (dx * dx + dz * dz <= radiusSq) {
                    for (int y = origin.getBlockY() - halfHeight; y <= origin.getBlockY() + halfHeight; y++) {
                        if (y < minHeight || y >= maxHeight) continue;

                        Location target = new Location(context.level().getWorld(), x, y, z);
                        
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
     * Configuration record for the disk feature.
     *
     * @param stateProvider The dynamic provider supplying the blocks for the disk.
     * @param radius        The horizontal radius of the disk.
     * @param halfHeight    The vertical thickness offset applied above and below the origin Y.
     * @param targets       The list of allowed block info targets that the disk can replace.
     */
    public record Config(BlockStateProvider stateProvider, int radius, int halfHeight, List<BlockInfo> targets) implements FeatureConfig {

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
                int radius = Codecs.INT.decode(ops, map.get(ops.createString("radius")));
                int halfHeight = Codecs.INT.decode(ops, map.get(ops.createString("half_height")));
                List<BlockInfo> targets = ExtraCodecs.BLOCK_INFO.list().decode(ops, map.get(ops.createString("targets")));

                return new Config(stateProvider, radius, halfHeight, targets);
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
                map.put(ops.createString("radius"), Codecs.INT.encode(ops, value.radius));
                map.put(ops.createString("half_height"), Codecs.INT.encode(ops, value.halfHeight));
                map.put(ops.createString("targets"), ExtraCodecs.BLOCK_INFO.list().encode(ops, value.targets));

                return ops.createMap(map);
            }
        };
    }
}