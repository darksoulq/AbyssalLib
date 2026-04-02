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
 * A world generation feature that scatters blocks dynamically around an origin point.
 * <p>
 * Upgraded to utilize the Block State Provider API, allowing the scattered patch
 * to consist of randomized or noise-driven block mixtures rather than a single static type.
 */
public class BlockPatchFeature extends Feature<BlockPatchFeature.Config> {

    /**
     * Constructs a new BlockPatchFeature with its associated configuration codec.
     */
    public BlockPatchFeature() {
        super(Config.CODEC);
    }

    /**
     * Executes the scattering placement logic using the state provider.
     *
     * @param context The feature place context providing world access and configuration.
     * @return True if at least one block was successfully placed.
     */
    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        int placedCount = 0;
        Random random = context.random();
        Location origin = context.origin();
        Config config = context.config();

        int minHeight = context.level().getWorld().getMinHeight();
        int maxHeight = context.level().getWorld().getMaxHeight();

        for (int i = 0; i < config.tries(); i++) {
            int dx = origin.getBlockX() + random.nextInt(config.xzSpread() * 2 + 1) - config.xzSpread();
            int dy = origin.getBlockY() + random.nextInt(config.ySpread() * 2 + 1) - config.ySpread();
            int dz = origin.getBlockZ() + random.nextInt(config.xzSpread() * 2 + 1) - config.xzSpread();

            if (dy < minHeight || dy >= maxHeight) {
                continue;
            }

            Location target = new Location(context.level().getWorld(), dx, dy, dz);
            
            if (WorldGenUtils.isValidBlock(context.level(), target, config.targets())) {
                BlockInfo stateToPlace = config.stateProvider().getState(random, target);
                if (stateToPlace != null) {
                    WorldGenUtils.placeBlock(context.level(), target, stateToPlace);
                    placedCount++;
                }
            }
        }

        return placedCount > 0;
    }

    /**
     * Specifies the procedural generation phase in which this feature executes.
     *
     * @return The VEGETAL_DECORATION generation phase.
     */
    @Override
    public GenerationPhase getPhase(Config config) {
        return GenerationPhase.VEGETAL_DECORATION;
    }

    /**
     * Configuration record for the block patch feature.
     *
     * @param tries         The total number of placement attempts within the patch radius.
     * @param xzSpread      The maximum horizontal offset from the origin on the X and Z axes.
     * @param ySpread       The maximum vertical offset from the origin on the Y axis.
     * @param stateProvider The dynamic provider supplying the blocks to be scattered.
     * @param targets       The list of allowed target block identifiers that can be overwritten.
     */
    public record Config(int tries, int xzSpread, int ySpread, BlockStateProvider stateProvider, List<BlockInfo> targets) implements FeatureConfig {

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
                int tries = Codecs.INT.decode(ops, map.get(ops.createString("tries")));
                int xzSpread = Codecs.INT.decode(ops, map.get(ops.createString("xz_spread")));
                int ySpread = Codecs.INT.decode(ops, map.get(ops.createString("y_spread")));
                BlockStateProvider stateProvider = BlockStateProvider.CODEC.decode(ops, map.get(ops.createString("state_provider")));
                List<BlockInfo> targets = ExtraCodecs.BLOCK_INFO.list().decode(ops, map.get(ops.createString("targets")));
                
                return new Config(tries, xzSpread, ySpread, stateProvider, targets);
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
                map.put(ops.createString("tries"), Codecs.INT.encode(ops, value.tries));
                map.put(ops.createString("xz_spread"), Codecs.INT.encode(ops, value.xzSpread));
                map.put(ops.createString("y_spread"), Codecs.INT.encode(ops, value.ySpread));
                map.put(ops.createString("state_provider"), BlockStateProvider.CODEC.encode(ops, value.stateProvider));
                map.put(ops.createString("targets"), ExtraCodecs.BLOCK_INFO.list().encode(ops, value.targets));
                
                return ops.createMap(map);
            }
        };
    }
}