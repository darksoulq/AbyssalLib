package com.github.darksoulq.abyssallib.world.gen.feature.impl;

import com.github.darksoulq.abyssallib.common.serialization.BlockInfo;
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.world.gen.feature.Feature;
import com.github.darksoulq.abyssallib.world.gen.feature.FeatureConfig;
import com.github.darksoulq.abyssallib.world.gen.feature.FeaturePlaceContext;
import com.github.darksoulq.abyssallib.world.gen.feature.GenerationPhase;
import com.github.darksoulq.abyssallib.world.gen.internal.WorldGenUtils;
import com.github.darksoulq.abyssallib.world.gen.state.provider.BlockStateProvider;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A world generation feature responsible for placing blocks that cling to solid surfaces.
 * <p>
 * This feature checks the immediately adjacent blocks around the origin. If any adjacent
 * block matches the allowed placement materials, it successfully places the specified
 * organic growth (e.g., glow lichen, vines, or sculk veins).
 */
public class MultifaceGrowthFeature extends Feature<MultifaceGrowthFeature.Config> {

    /** The standard array of 6 adjacent block faces used for boundary calculation. */
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
                List<BlockInfo> canPlaceOn = ExtraCodecs.BLOCK_INFO.list().decode(ops, map.get(ops.createString("can_place_on")));
                
                return new Config(stateProvider, canPlaceOn);
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
                map.put(ops.createString("can_place_on"), ExtraCodecs.BLOCK_INFO.list().encode(ops, value.canPlaceOn));
                
                return ops.createMap(map);
            }
        };
    }
}