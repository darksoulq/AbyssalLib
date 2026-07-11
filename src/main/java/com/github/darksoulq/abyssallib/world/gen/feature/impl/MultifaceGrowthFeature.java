package com.github.darksoulq.abyssallib.world.gen.feature.impl;

import com.github.darksoulq.abyssallib.common.serialization.BlockInfo;
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.common.serialization.RecordBuilder;
import com.github.darksoulq.abyssallib.world.gen.feature.Feature;
import com.github.darksoulq.abyssallib.world.gen.feature.FeatureConfig;
import com.github.darksoulq.abyssallib.world.gen.feature.FeaturePlaceContext;
import com.github.darksoulq.abyssallib.world.gen.feature.GenerationPhase;
import com.github.darksoulq.abyssallib.world.gen.internal.WorldGenUtils;
import com.github.darksoulq.abyssallib.world.gen.state.provider.BlockStateProvider;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import java.util.Collections;
import java.util.List;

/**
 * A world generation feature responsible for placing blocks that cling to solid surfaces.
 * <p>
 * This feature checks the immediately adjacent blocks around the origin. If any adjacent
 * block matches the allowed placement materials, it successfully places the specified
 * organic growth (e.g., glow lichen, vines, or sculk veins).
 */
public class MultifaceGrowthFeature extends Feature<MultifaceGrowthFeature.Config> {

    /**
     * The standard array of 6 adjacent block faces used for boundary calculation.
     */
    private static final BlockFace[] FACES = {
        BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST
    };

    /**
     * Constructs a new MultifaceGrowthFeature with its associated configuration codec.
     */
    public MultifaceGrowthFeature() {
        super(Config.CODEC);
    }

    /**
     * Executes the boundary evaluation and potential placement of the growth block.
     *
     * @param context The feature place context providing world access and configuration.
     * @return True if a valid adjacent block was found and the growth was placed.
     */
    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        Location origin = context.origin();
        Config config = context.config();

        Material currentMat = context.level().getType(origin.getBlockX(), origin.getBlockY(), origin.getBlockZ());
        if (currentMat != Material.AIR && currentMat != Material.CAVE_AIR && currentMat != Material.WATER) {
            return false;
        }

        boolean validPlacement = false;

        for (BlockFace face : FACES) {
            Location neighbor = origin.clone().add(face.getModX(), face.getModY(), face.getModZ());

            if (WorldGenUtils.isValidBlock(context.level(), neighbor, config.canPlaceOn())) {
                validPlacement = true;
                break;
            }
        }

        if (validPlacement) {
            BlockInfo stateToPlace = config.stateProvider().getState(context.random(), origin);
            if (stateToPlace != null) {
                WorldGenUtils.placeBlock(context.level(), origin, stateToPlace);
                return true;
            }
        }

        return false;
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
     * Configuration record for the multiface growth feature.
     *
     * @param stateProvider The dynamic provider representing the organic growth to place.
     * @param canPlaceOn    The list of block info targets considered valid support blocks.
     */
    public record Config(BlockStateProvider stateProvider, List<BlockInfo> canPlaceOn) implements FeatureConfig {

        /**
         * The codec for serializing and deserializing the configuration.
         */
        public static final Codec<Config> CODEC = RecordBuilder.create(instance -> instance.group(
            BlockStateProvider.CODEC.fieldOf("state_provider").forGetter(Config.class, Config::stateProvider),
            ExtraCodecs.BLOCK_INFO.list().optionalFieldOf("can_place_on", Collections.emptyList()).forGetter(Config.class, Config::canPlaceOn)
        ).apply(instance, Config::new)).describe("MultifaceGrowthConfig");
    }
}