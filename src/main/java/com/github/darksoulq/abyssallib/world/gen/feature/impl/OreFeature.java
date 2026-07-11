package com.github.darksoulq.abyssallib.world.gen.feature.impl;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.world.gen.feature.Feature;
import com.github.darksoulq.abyssallib.world.gen.feature.FeatureConfig;
import com.github.darksoulq.abyssallib.world.gen.feature.FeaturePlaceContext;
import com.github.darksoulq.abyssallib.world.gen.feature.GenerationPhase;
import com.github.darksoulq.abyssallib.world.gen.internal.WorldGenUtils;
import com.github.darksoulq.abyssallib.world.gen.state.provider.BlockStateProvider;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Random;

/**
 * A world generation feature that generates clusters of ore veins.
 */
public class OreFeature extends Feature<OreFeature.Config> {

    /**
     * Constructs a new OreFeature with the associated configuration codec.
     */
    public OreFeature() {
        super(Config.CODEC);
    }

    /**
     * Executes the placement logic for the ore cluster.
     *
     * @param context The feature place context providing world access, origin, random source, and configuration.
     * @return True if the feature was successfully triggered.
     */
    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        Random random = context.random();
        Vector origin = context.origin().toVector();
        int size = context.config().size;

        float angle = random.nextFloat() * (float) Math.PI;
        float radius = (float) size / 8.0F;

        double startX = origin.getX() + Math.sin(angle) * radius;
        double endX = origin.getX() - Math.sin(angle) * radius;
        double startZ = origin.getZ() + Math.cos(angle) * radius;
        double endZ = origin.getZ() - Math.cos(angle) * radius;
        double startY = origin.getY() + random.nextInt(3) - 2;
        double endY = origin.getY() + random.nextInt(3) - 2;

        int minHeight = context.level().world().getMinHeight();
        int maxHeight = context.level().world().getMaxHeight();

        for (int l = 0; l < size; ++l) {
            float progress = (float) l / (float) size;
            double currentX = startX + (endX - startX) * progress;
            double currentY = startY + (endY - startY) * progress;
            double currentZ = startZ + (endZ - startZ) * progress;

            double spread = random.nextDouble() * size / 16.0D;
            double widthMod = (Math.sin(Math.PI * progress) + 1.0F) * spread + 1.0D;
            double heightMod = (Math.sin(Math.PI * progress) + 1.0F) * spread + 1.0D;

            int minXBound = (int) Math.floor(currentX - widthMod / 2.0D);
            int minYBound = (int) Math.floor(currentY - heightMod / 2.0D);
            int minZBound = (int) Math.floor(currentZ - widthMod / 2.0D);
            int maxXBound = (int) Math.floor(currentX + widthMod / 2.0D);
            int maxYBound = (int) Math.floor(currentY + heightMod / 2.0D);
            int maxZBound = (int) Math.floor(currentZ + widthMod / 2.0D);

            for (int x = minXBound; x <= maxXBound; ++x) {
                double xDist = ((double) x + 0.5D - currentX) / (widthMod / 2.0D);
                if (xDist * xDist >= 1.0D) continue;

                for (int y = minYBound; y <= maxYBound; ++y) {
                    double yDist = ((double) y + 0.5D - currentY) / (heightMod / 2.0D);
                    if (xDist * xDist + yDist * yDist >= 1.0D) continue;

                    for (int z = minZBound; z <= maxZBound; ++z) {
                        double zDist = ((double) z + 0.5D - currentZ) / (widthMod / 2.0D);

                        if (xDist * xDist + yDist * yDist + zDist * zDist < 1.0D && minHeight <= y && y < maxHeight) {
                            Location loc = new Location(context.level().world(), x, y, z);

                            for (Target target : context.config().targets) {
                                if (WorldGenUtils.isValidBlock(context.level(), loc, target.target)) {
                                    BlockInfo stateToPlace = target.stateProvider().getState(random, loc);
                                    if (stateToPlace != null) {
                                        WorldGenUtils.placeBlock(context.level(), loc, stateToPlace);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Specifies the procedural generation phase in which this feature executes.
     *
     * @return The UNDERGROUND_ORES generation phase.
     */
    @Override
    public GenerationPhase getPhase(Config config) {
        return GenerationPhase.UNDERGROUND_ORES;
    }

    /**
     * A record representing a specific replacement rule for the ore feature.
     *
     * @param target        A list of block info targets that should be replaced if encountered.
     * @param stateProvider The dynamic provider supplying the blocks to replace the target with.
     */
    public record Target(List<BlockInfo> target, BlockStateProvider stateProvider) {

        /**
         * The codec for serializing and deserializing a target rule.
         */
        public static final Codec<Target> CODEC = RecordBuilder.create(instance -> instance.group(
            ExtraCodecs.BLOCK_INFO.list().fieldOf("target").forGetter(Target.class, Target::target),
            BlockStateProvider.CODEC.fieldOf("state_provider").forGetter(Target.class, Target::stateProvider)
        ).apply(instance, Target::new)).describe("OreTarget");
    }

    /**
     * Configuration record for the ore feature.
     *
     * @param targets A list of target rules defining what to replace and with what.
     * @param size    The relative size and volume bounds of the ore vein.
     */
    public record Config(List<Target> targets, int size) implements FeatureConfig {

        /**
         * The codec for serializing and deserializing the configuration.
         */
        public static final Codec<Config> CODEC = RecordBuilder.create(instance -> instance.group(
            Target.CODEC.list().fieldOf("targets").forGetter(Config.class, Config::targets),
            Codecs.INT.fieldOf("size").forGetter(Config.class, Config::size)
        ).apply(instance, Config::new)).describe("OreConfig");
    }
}