package com.github.darksoulq.abyssallib.world.gen.feature.impl;

import com.github.darksoulq.abyssallib.common.serialization.*;
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
 * A world generation feature responsible for creating isolated liquid source blocks (springs).
 */
public class SpringFeature extends Feature<SpringFeature.Config> {

    /** The standard array of 6 adjacent block faces used for boundary calculation. */
    private static final BlockFace[] FACES = {
        BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST
    };

    /**
     * Constructs a new SpringFeature with its associated configuration codec.
     */
    public SpringFeature() {
        super(Config.CODEC);
    }

    /**
     * Executes the strict boundary evaluation and potential placement of the spring block.
     *
     * @param context The feature place context providing world access and configuration.
     * @return True if the surrounding geography matched the config and the spring was placed.
     */
    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        Location origin = context.origin();
        Config config = context.config();

        if (!WorldGenUtils.isValidBlock(context.level(), origin, config.rock())) {
            return false;
        }

        Location blockBelow = origin.clone().add(0, -1, 0);
        if (config.requiresBlockBelow() && !WorldGenUtils.isValidBlock(context.level(), blockBelow, config.rock())) {
            return false;
        }

        int rockCount = 0;
        int holeCount = 0;

        for (BlockFace face : FACES) {
            Location neighbor = origin.clone().add(face.getModX(), face.getModY(), face.getModZ());

            if (WorldGenUtils.isValidBlock(context.level(), neighbor, config.rock())) {
                rockCount++;
            } else if (context.level().getType(neighbor.getBlockX(), neighbor.getBlockY(), neighbor.getBlockZ()) == Material.AIR) {
                holeCount++;
            }
        }

        if (rockCount == config.validNeighbors() && holeCount == config.holeCount()) {
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
     * @return The FLUID_SPRINGS generation phase.
     */
    @Override
    public GenerationPhase getPhase(Config config) {
        return GenerationPhase.FLUID_SPRINGS;
    }

    /**
     * Configuration record for the spring feature.
     *
     * @param stateProvider       The dynamic block provider representing the core liquid to place.
     * @param rock                The list of target blocks considered valid encasing materials.
     * @param requiresBlockBelow  Flag dictating if the block directly beneath the origin must be rock.
     * @param holeCount           The exact number of adjacent blocks that must be air for placement.
     * @param validNeighbors      The exact number of adjacent blocks that must be rock for placement.
     */
    public record Config(BlockStateProvider stateProvider, List<BlockInfo> rock, boolean requiresBlockBelow, int holeCount, int validNeighbors) implements FeatureConfig {

        /**
         * The codec for serializing and deserializing the configuration.
         */
        public static final Codec<Config> CODEC = RecordBuilder.create(instance -> instance.group(
            BlockStateProvider.CODEC.fieldOf("state_provider").forGetter(Config.class, Config::stateProvider),
            ExtraCodecs.BLOCK_INFO.list().optionalFieldOf("rock", Collections.emptyList()).forGetter(Config.class, Config::rock),
            Codecs.BOOLEAN.optionalFieldOf("requires_block_below", true).forGetter(Config.class, Config::requiresBlockBelow),
            Codecs.INT.optionalFieldOf("hole_count", 1).forGetter(Config.class, Config::holeCount),
            Codecs.INT.optionalFieldOf("valid_neighbors", 5).forGetter(Config.class, Config::validNeighbors)
        ).apply(instance, Config::new)).describe("SpringConfig");
    }
}