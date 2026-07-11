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

            if (target.getBlockY() < context.level().world().getMinHeight() || target.getBlockY() >= context.level().world().getMaxHeight()) {
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
    public record Config(int minHeight, int maxHeight, boolean upward, boolean stopOnInvalid,
                         BlockStateProvider stateProvider, List<BlockInfo> targets) implements FeatureConfig {

        /**
         * The codec for serializing and deserializing the configuration.
         */
        public static final Codec<Config> CODEC = RecordBuilder.create(instance -> instance.group(
            Codecs.INT.optionalFieldOf("min_height", 1).forGetter(Config.class, Config::minHeight),
            Codecs.INT.optionalFieldOf("max_height", 5).forGetter(Config.class, Config::maxHeight),
            Codecs.BOOLEAN.optionalFieldOf("upward", true).forGetter(Config.class, Config::upward),
            Codecs.BOOLEAN.optionalFieldOf("stop_on_invalid", true).forGetter(Config.class, Config::stopOnInvalid),
            BlockStateProvider.CODEC.fieldOf("state_provider").forGetter(Config.class, Config::stateProvider),
            ExtraCodecs.BLOCK_INFO.list().optionalFieldOf("targets", Collections.emptyList()).forGetter(Config.class, Config::targets)
        ).apply(instance, Config::new)).describe("PillarConfig");
    }
}