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

/**
 * A world generation feature that generates a continuous vertical pillar of blocks.
 */
public class PillarFeature extends Feature<PillarFeature.Config> {

    /**
     * Constructs a new PillarFeature with its associated configuration codec.
     */
    public PillarFeature() {
        super(Config.CODEC);
    }

    /**
     * Executes the placement logic to build the vertical column.
     *
     * @param context The feature place context providing world access and configuration.
     * @return True if at least one block was successfully placed.
     */
    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        Location origin = context.origin();
        Config config = context.config();
        
        int range = Math.max(1, config.maxHeight() - config.minHeight() + 1);
        int height = config.minHeight() + context.random().nextInt(range);
        int placedCount = 0;

        for (int i = 0; i < height; i++) {
            Location target = origin.clone().add(0, config.upward() ? i : -i, 0);
            
            if (target.getBlockY() < context.level().getWorld().getMinHeight() || target.getBlockY() >= context.level().getWorld().getMaxHeight()) {
                break;
            }

            if (WorldGenUtils.isValidBlock(context.level(), target, config.targets())) {
                BlockInfo stateToPlace = config.stateProvider().getState(context.random(), target);
                if (stateToPlace != null) {
                    WorldGenUtils.placeBlock(context.level(), target, stateToPlace);
                    placedCount++;
                }
            } else if (config.stopOnInvalid()) {
                break;
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
     * Configuration record for the pillar feature.
     *
     * @param minHeight     The minimum guaranteed block length of the pillar.
     * @param maxHeight     The maximum possible block length of the pillar.
     * @param upward        True to generate upwards from the origin, false to generate downwards.
     * @param stopOnInvalid True to abort the rest of the pillar if an invalid target is hit.
     * @param stateProvider The dynamic provider supplying the blocks to build the pillar.
     * @param targets       The list of allowed target block identifiers that can be overwritten.
     */
    public record Config(int minHeight, int maxHeight, boolean upward, boolean stopOnInvalid, BlockStateProvider stateProvider, List<BlockInfo> targets) implements FeatureConfig {

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
                
                int minHeight = Codecs.INT.decode(ops, map.get(ops.createString("min_height")));
                int maxHeight = Codecs.INT.decode(ops, map.get(ops.createString("max_height")));
                boolean upward = Codecs.BOOLEAN.decode(ops, map.get(ops.createString("upward")));
                boolean stopOnInvalid = Codecs.BOOLEAN.decode(ops, map.get(ops.createString("stop_on_invalid")));
                BlockStateProvider stateProvider = BlockStateProvider.CODEC.decode(ops, map.get(ops.createString("state_provider")));
                List<BlockInfo> targets = ExtraCodecs.BLOCK_INFO.list().decode(ops, map.get(ops.createString("targets")));
                
                return new Config(minHeight, maxHeight, upward, stopOnInvalid, stateProvider, targets);
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
                
                map.put(ops.createString("min_height"), Codecs.INT.encode(ops, value.minHeight));
                map.put(ops.createString("max_height"), Codecs.INT.encode(ops, value.maxHeight));
                map.put(ops.createString("upward"), Codecs.BOOLEAN.encode(ops, value.upward));
                map.put(ops.createString("stop_on_invalid"), Codecs.BOOLEAN.encode(ops, value.stopOnInvalid));
                map.put(ops.createString("state_provider"), BlockStateProvider.CODEC.encode(ops, value.stateProvider));
                map.put(ops.createString("targets"), ExtraCodecs.BLOCK_INFO.list().encode(ops, value.targets));
                
                return ops.createMap(map);
            }
        };
    }
}