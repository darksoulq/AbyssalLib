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

import java.util.Collections;
import java.util.List;
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
        public static final Codec<Config> CODEC = RecordBuilder.create(instance -> instance.group(
            BlockStateProvider.CODEC.fieldOf("ground_provider").forGetter(Config.class, Config::groundProvider),
            BlockStateProvider.CODEC.fieldOf("vegetation_provider").forGetter(Config.class, Config::vegetationProvider),
            Codecs.INT.optionalFieldOf("radius", 5).forGetter(Config.class, Config::radius),
            Codecs.INT.optionalFieldOf("depth", 1).forGetter(Config.class, Config::depth),
            Codecs.INT.optionalFieldOf("vegetation_chance", 10).forGetter(Config.class, Config::vegetationChance),
            ExtraCodecs.BLOCK_INFO.list().optionalFieldOf("replaceable_ground", Collections.emptyList()).forGetter(Config.class, Config::replaceableGround)
        ).apply(instance, Config::new)).describe("VegetationPatchConfig");
    }
}