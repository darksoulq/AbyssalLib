package com.github.darksoulq.abyssallib.world.gen.feature.impl;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.world.gen.feature.Feature;
import com.github.darksoulq.abyssallib.world.gen.feature.FeatureConfig;
import com.github.darksoulq.abyssallib.world.gen.feature.FeaturePlaceContext;
import com.github.darksoulq.abyssallib.world.gen.feature.GenerationPhase;
import com.github.darksoulq.abyssallib.world.gen.internal.WorldGenUtils;
import com.github.darksoulq.abyssallib.world.gen.state.provider.BlockStateProvider;
import org.bukkit.Location;

import java.util.Collections;
import java.util.List;
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
        public static final Codec<Config> CODEC = RecordBuilder.create(instance -> instance.group(
            Codecs.INT.optionalFieldOf("tries", 64).forGetter(Config.class, Config::tries),
            Codecs.INT.optionalFieldOf("xz_spread", 7).forGetter(Config.class, Config::xzSpread),
            Codecs.INT.optionalFieldOf("y_spread", 3).forGetter(Config.class, Config::ySpread),
            BlockStateProvider.CODEC.fieldOf("state_provider").forGetter(Config.class, Config::stateProvider),
            ExtraCodecs.BLOCK_INFO.list().optionalFieldOf("targets", Collections.emptyList()).forGetter(Config.class, Config::targets)
        ).apply(instance, Config::new)).describe("BlockPatchConfig");
    }
}