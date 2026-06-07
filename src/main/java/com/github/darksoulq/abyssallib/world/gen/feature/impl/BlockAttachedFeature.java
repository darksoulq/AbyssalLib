package com.github.darksoulq.abyssallib.world.gen.feature.impl;

import com.github.darksoulq.abyssallib.common.serialization.BlockInfo;
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.common.serialization.RecordBuilder;
import com.github.darksoulq.abyssallib.world.gen.feature.Feature;
import com.github.darksoulq.abyssallib.world.gen.feature.FeatureConfig;
import com.github.darksoulq.abyssallib.world.gen.feature.FeaturePlaceContext;
import com.github.darksoulq.abyssallib.world.gen.internal.WorldGenUtils;
import com.github.darksoulq.abyssallib.world.gen.state.provider.BlockStateProvider;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;

import java.util.Collections;
import java.util.List;

/**
 * A world generation feature that places a single block attached to a specific face
 * of an existing, valid supporting block.
 * <p>
 * This feature evaluates a list of allowed directions. If the block immediately
 * adjacent to the origin in one of those directions matches the allowed support blocks,
 * the feature successfully places its state at the origin.
 */
public class BlockAttachedFeature extends Feature<BlockAttachedFeature.Config> {

    /**
     * Constructs a new BlockAttachedFeature with its associated configuration codec.
     */
    public BlockAttachedFeature() {
        super(Config.CODEC);
    }

    /**
     * Executes the placement logic by verifying structural support in the allowed directions.
     *
     * @param context The feature place context providing world access and configuration.
     * @return True if a valid support was found and the block was placed.
     */
    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        Location origin = context.origin();
        Config config = context.config();

        if (!WorldGenUtils.isValidBlock(context.level(), origin, config.targets())) {
            return false;
        }

        for (BlockFace face : config.directions()) {
            Location supportLoc = origin.clone().add(face.getModX(), face.getModY(), face.getModZ());

            if (WorldGenUtils.isValidBlock(context.level(), supportLoc, config.canAttachTo())) {
                BlockInfo stateToPlace = config.stateProvider().getState(context.random(), origin);
                if (stateToPlace != null) {
                    WorldGenUtils.placeBlock(context.level(), origin, stateToPlace);
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Configuration record for the block attached feature.
     *
     * @param stateProvider The dynamic provider supplying the block to be placed.
     * @param targets       The list of allowed block info targets that can be overwritten at the origin.
     * @param canAttachTo   The list of block info targets that are considered valid structural support.
     * @param directions    The list of block faces to check for structural support relative to the origin.
     */
    public record Config(BlockStateProvider stateProvider, List<BlockInfo> targets, List<BlockInfo> canAttachTo, List<BlockFace> directions) implements FeatureConfig {

        /**
         * The codec for serializing and deserializing the configuration.
         */
        public static final Codec<Config> CODEC = RecordBuilder.create(instance -> instance.group(
            BlockStateProvider.CODEC.fieldOf("state_provider").forGetter(Config.class, Config::stateProvider),
            ExtraCodecs.BLOCK_INFO.list().optionalFieldOf("targets", Collections.emptyList()).forGetter(Config.class, Config::targets),
            ExtraCodecs.BLOCK_INFO.list().optionalFieldOf("can_attach_to", Collections.emptyList()).forGetter(Config.class, Config::canAttachTo),
            Codec.enumCodec(BlockFace.class).list().optionalFieldOf("directions", Collections.emptyList()).forGetter(Config.class, Config::directions)
        ).apply(instance, Config::new)).describe("BlockAttachedFeatureConfig");
    }
}