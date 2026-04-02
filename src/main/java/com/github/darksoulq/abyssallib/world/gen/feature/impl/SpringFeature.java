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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                List<BlockInfo> rock = ExtraCodecs.BLOCK_INFO.list().decode(ops, map.get(ops.createString("rock")));
                boolean requiresBlockBelow = Codecs.BOOLEAN.decode(ops, map.get(ops.createString("requires_block_below")));
                int holeCount = Codecs.INT.decode(ops, map.get(ops.createString("hole_count")));
                int validNeighbors = Codecs.INT.decode(ops, map.get(ops.createString("valid_neighbors")));
                
                return new Config(stateProvider, rock, requiresBlockBelow, holeCount, validNeighbors);
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
                map.put(ops.createString("rock"), ExtraCodecs.BLOCK_INFO.list().encode(ops, value.rock));
                map.put(ops.createString("requires_block_below"), Codecs.BOOLEAN.encode(ops, value.requiresBlockBelow));
                map.put(ops.createString("hole_count"), Codecs.INT.encode(ops, value.holeCount));
                map.put(ops.createString("valid_neighbors"), Codecs.INT.encode(ops, value.validNeighbors));
                
                return ops.createMap(map);
            }
        };
    }
}