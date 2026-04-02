package com.github.darksoulq.abyssallib.world.gen.feature.impl;

import com.github.darksoulq.abyssallib.common.serialization.BlockInfo;
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.world.gen.feature.Feature;
import com.github.darksoulq.abyssallib.world.gen.feature.FeatureConfig;
import com.github.darksoulq.abyssallib.world.gen.feature.FeaturePlaceContext;
import com.github.darksoulq.abyssallib.world.gen.internal.WorldGenUtils;
import com.github.darksoulq.abyssallib.world.gen.state.provider.BlockStateProvider;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public record Config(
            BlockStateProvider stateProvider,
            List<BlockInfo> targets,
            List<BlockInfo> canAttachTo,
            List<BlockFace> directions
    ) implements FeatureConfig {

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
                List<BlockInfo> targets = ExtraCodecs.BLOCK_INFO.list().decode(ops, map.get(ops.createString("targets")));
                List<BlockInfo> canAttachTo = ExtraCodecs.BLOCK_INFO.list().decode(ops, map.get(ops.createString("can_attach_to")));
                List<BlockFace> directions = Codec.enumCodec(BlockFace.class).list().decode(ops, map.get(ops.createString("directions")));
                
                return new Config(stateProvider, targets, canAttachTo, directions);
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
                map.put(ops.createString("targets"), ExtraCodecs.BLOCK_INFO.list().encode(ops, value.targets));
                map.put(ops.createString("can_attach_to"), ExtraCodecs.BLOCK_INFO.list().encode(ops, value.canAttachTo));
                map.put(ops.createString("directions"), Codec.enumCodec(BlockFace.class).list().encode(ops, value.directions));
                
                return ops.createMap(map);
            }
        };
    }
}