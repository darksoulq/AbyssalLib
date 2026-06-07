package com.github.darksoulq.abyssallib.world.gen.feature.impl;

import com.github.darksoulq.abyssallib.common.serialization.BlockInfo;
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.RecordBuilder;
import com.github.darksoulq.abyssallib.world.gen.feature.Feature;
import com.github.darksoulq.abyssallib.world.gen.feature.FeatureConfig;
import com.github.darksoulq.abyssallib.world.gen.feature.FeaturePlaceContext;
import com.github.darksoulq.abyssallib.world.gen.feature.GenerationPhase;
import com.github.darksoulq.abyssallib.world.gen.internal.WorldGenUtils;
import com.github.darksoulq.abyssallib.world.gen.state.provider.BlockStateProvider;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Random;

/**
 * A specialized patch feature for scattering vegetation strictly inside water columns.
 * <p>
 * This is designed for placing aquatic plants like seagrass, kelp, or corals. It ensures
 * that the generated vegetation replaces existing water blocks while remaining properly
 * supported by solid blocks underneath.
 */
public class WaterloggedVegetationFeature extends Feature<WaterloggedVegetationFeature.Config> {

    /**
     * Constructs a new WaterloggedVegetationFeature with its associated configuration codec.
     */
    public WaterloggedVegetationFeature() {
        super(Config.CODEC);
    }

    /**
     * Executes the aquatic scattering placement logic.
     *
     * @param context The feature place context providing world access and configuration.
     * @return True if at least one waterlogged plant was successfully placed.
     */
    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        int placedCount = 0;
        Random random = context.random();
        Location origin = context.origin();
        Config config = context.config();

        int minHeight = context.level().getWorld().getMinHeight();
        int maxHeight = context.level().getWorld().getMaxHeight();

        for (int i = 0; i < config.tries(); i++) {
            int dx = origin.getBlockX() + random.nextInt(config.xzSpread() * 2 + 1) - config.xzSpread();
            int dy = origin.getBlockY() + random.nextInt(config.ySpread() * 2 + 1) - config.ySpread();
            int dz = origin.getBlockZ() + random.nextInt(config.xzSpread() * 2 + 1) - config.xzSpread();

            if (dy < minHeight || dy >= maxHeight) {
                continue;
            }

            Location target = new Location(context.level().getWorld(), dx, dy, dz);
            Material currentMat = context.level().getType(target.getBlockX(), target.getBlockY(), target.getBlockZ());

            if (currentMat == Material.WATER) {
                Location below = target.clone().add(0, -1, 0);
                Material belowMat = context.level().getType(below.getBlockX(), below.getBlockY(), below.getBlockZ());

                if (belowMat.isSolid() && belowMat != Material.ICE) {
                    BlockInfo stateToPlace = config.stateProvider().getState(random, target);
                    if (stateToPlace != null) {
                        WorldGenUtils.placeBlock(context.level(), target, stateToPlace);
                        placedCount++;
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
     * Configuration record for the waterlogged vegetation feature.
     *
     * @param tries         The total number of placement attempts within the scatter radius.
     * @param xzSpread      The maximum horizontal offset from the origin on the X and Z axes.
     * @param ySpread       The maximum vertical offset from the origin on the Y axis.
     * @param stateProvider The dynamic provider supplying the aquatic plants to be scattered.
     */
    public record Config(int tries, int xzSpread, int ySpread, BlockStateProvider stateProvider) implements FeatureConfig {

        /**
         * The codec for serializing and deserializing the configuration.
         */
        public static final Codec<Config> CODEC = RecordBuilder.create(instance -> instance.group(
            Codecs.INT.optionalFieldOf("tries", 64).forGetter(Config.class, Config::tries),
            Codecs.INT.optionalFieldOf("xz_spread", 7).forGetter(Config.class, Config::xzSpread),
            Codecs.INT.optionalFieldOf("y_spread", 3).forGetter(Config.class, Config::ySpread),
            BlockStateProvider.CODEC.fieldOf("state_provider").forGetter(Config.class, Config::stateProvider)
        ).apply(instance, Config::new)).describe("WaterloggedVegetationConfig");
    }
}