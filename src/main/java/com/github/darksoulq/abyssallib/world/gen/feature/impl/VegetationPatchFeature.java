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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * A sophisticated world generation feature that alters the terrain surface and places
 * vegetation on top of the newly altered ground.
 * <p>
 * This is used for generating biomes like Lush Caves or Mossy patches, where the
 * stone/dirt must first be converted into a base block (e.g., Moss) before
 * vegetation (e.g., Azaleas) is scattered across it.
 */
public class VegetationPatchFeature extends Feature<VegetationPatchFeature.Config> {

    /**
     * Constructs a new VegetationPatchFeature with its associated configuration codec.
     */
    public VegetationPatchFeature() {
        super(Config.CODEC);
    }

    /**
     * Executes the terrain alteration and vegetation placement logic.
     *
     * @param context The feature place context providing world access and configuration.
     * @return True if at least one ground block was replaced.
     */
    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        Location origin = context.origin();
        Config config = context.config();
        Random random = context.random();
        int placedCount = 0;

        int radius = config.radius();
        int radiusSq = radius * radius;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx * dx + dz * dz > radiusSq) {
                    continue;
                }

                Location currentColumn = origin.clone().add(dx, 0, dz);
                boolean replacedGround = false;
                Location highestReplaced = null;

                for (int yOffset = 0; yOffset >= -config.depth(); yOffset--) {
                    Location target = currentColumn.clone().add(0, yOffset, 0);
                    
                    if (WorldGenUtils.isValidBlock(context.level(), target, config.replaceableGround())) {
                        BlockInfo groundState = config.groundProvider().getState(random, target);
                        if (groundState != null) {
                            WorldGenUtils.placeBlock(context.level(), target, groundState);
                            replacedGround = true;
                            if (highestReplaced == null) {
                                highestReplaced = target.clone();
                            }
                            placedCount++;
                        }
                    } else if (replacedGround) {
                        break;
                    }
                }

                if (highestReplaced != null && random.nextInt(config.vegetationChance()) == 0) {
                    Location plantLoc = highestReplaced.clone().add(0, 1, 0);
                    Material currentMat = context.level().getType(plantLoc.getBlockX(), plantLoc.getBlockY(), plantLoc.getBlockZ());
                    
                    if (currentMat == Material.AIR || currentMat == Material.CAVE_AIR) {
                        BlockInfo vegState = config.vegetationProvider().getState(random, plantLoc);
                        if (vegState != null) {
                            WorldGenUtils.placeBlock(context.level(), plantLoc, vegState);
                        }
                    }
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
     * Configuration record for the vegetation patch feature.
     *
     * @param groundProvider      The block state provider defining the new ground terrain.
     * @param vegetationProvider  The block state provider defining the plants scattered on top.
     * @param radius              The horizontal radius of the terrain alteration patch.
     * @param depth               The vertical depth to which the ground will be replaced.
     * @param vegetationChance    The 1-in-X probability of placing a plant on an altered surface column.
     * @param replaceableGround   The list of blocks that are allowed to be converted into the new ground.
     */
    public record Config(
            BlockStateProvider groundProvider,
            BlockStateProvider vegetationProvider,
            int radius,
            int depth,
            int vegetationChance,
            List<BlockInfo> replaceableGround
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
                
                BlockStateProvider groundProvider = BlockStateProvider.CODEC.decode(ops, map.get(ops.createString("ground_provider")));
                BlockStateProvider vegetationProvider = BlockStateProvider.CODEC.decode(ops, map.get(ops.createString("vegetation_provider")));
                int radius = Codecs.INT.decode(ops, map.get(ops.createString("radius")));
                int depth = Codecs.INT.decode(ops, map.get(ops.createString("depth")));
                int vegetationChance = Codecs.INT.decode(ops, map.get(ops.createString("vegetation_chance")));
                List<BlockInfo> replaceableGround = ExtraCodecs.BLOCK_INFO.list().decode(ops, map.get(ops.createString("replaceable_ground")));

                return new Config(groundProvider, vegetationProvider, radius, depth, vegetationChance, replaceableGround);
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
                
                map.put(ops.createString("ground_provider"), BlockStateProvider.CODEC.encode(ops, value.groundProvider));
                map.put(ops.createString("vegetation_provider"), BlockStateProvider.CODEC.encode(ops, value.vegetationProvider));
                map.put(ops.createString("radius"), Codecs.INT.encode(ops, value.radius));
                map.put(ops.createString("depth"), Codecs.INT.encode(ops, value.depth));
                map.put(ops.createString("vegetation_chance"), Codecs.INT.encode(ops, value.vegetationChance));
                map.put(ops.createString("replaceable_ground"), ExtraCodecs.BLOCK_INFO.list().encode(ops, value.replaceableGround));

                return ops.createMap(map);
            }
        };
    }
}