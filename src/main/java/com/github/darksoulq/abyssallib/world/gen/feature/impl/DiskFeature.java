package com.github.darksoulq.abyssallib.world.gen.feature.impl;

import com.github.darksoulq.abyssallib.common.serialization.BlockInfo;
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.common.serialization.RecordBuilder;
import com.github.darksoulq.abyssallib.world.gen.feature.Feature;
import com.github.darksoulq.abyssallib.world.gen.feature.FeatureConfig;
import com.github.darksoulq.abyssallib.world.gen.feature.FeaturePlaceContext;
import com.github.darksoulq.abyssallib.world.gen.internal.WorldGenUtils;
import com.github.darksoulq.abyssallib.world.gen.state.provider.BlockStateProvider;
import org.bukkit.Location;

import java.util.Collections;
import java.util.List;

/**
 * A world generation feature that generates a flat, circular disk dynamically.
 * <p>
 * Upgraded to utilize the Block State Provider API, enabling features like
 * mixed gravel and sand ocean floors.
 */
public class DiskFeature extends Feature<DiskFeature.Config> {

    /**
     * Constructs a new DiskFeature with its associated configuration codec.
     */
    public DiskFeature() {
        super(Config.CODEC);
    }

    /**
     * Executes the circular placement logic centered on the given origin.
     *
     * @param context The feature place context providing world access and configuration.
     * @return True if at least one block was successfully placed.
     */
    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        Location origin = context.origin();
        Config config = context.config();
        int placedCount = 0;

        int minHeight = context.level().getWorld().getMinHeight();
        int maxHeight = context.level().getWorld().getMaxHeight();

        int radius = config.radius();
        int halfHeight = config.halfHeight();
        int radiusSq = radius * radius;

        for (int x = origin.getBlockX() - radius; x <= origin.getBlockX() + radius; x++) {
            for (int z = origin.getBlockZ() - radius; z <= origin.getBlockZ() + radius; z++) {
                int dx = x - origin.getBlockX();
                int dz = z - origin.getBlockZ();

                if (dx * dx + dz * dz <= radiusSq) {
                    for (int y = origin.getBlockY() - halfHeight; y <= origin.getBlockY() + halfHeight; y++) {
                        if (y < minHeight || y >= maxHeight) continue;

                        Location target = new Location(context.level().getWorld(), x, y, z);

                        if (WorldGenUtils.isValidBlock(context.level(), target, config.targets())) {
                            BlockInfo stateToPlace = config.stateProvider().getState(context.random(), target);
                            if (stateToPlace != null) {
                                WorldGenUtils.placeBlock(context.level(), target, stateToPlace);
                                placedCount++;
                            }
                        }
                    }
                }
            }
        }

        return placedCount > 0;
    }

    /**
     * Configuration record for the disk feature.
     *
     * @param stateProvider The dynamic provider supplying the blocks for the disk.
     * @param radius        The horizontal radius of the disk.
     * @param halfHeight    The vertical thickness offset applied above and below the origin Y.
     * @param targets       The list of allowed block info targets that the disk can replace.
     */
    public record Config(BlockStateProvider stateProvider, int radius, int halfHeight, List<BlockInfo> targets) implements FeatureConfig {

        /**
         * The codec for serializing and deserializing the configuration.
         */
        public static final Codec<Config> CODEC = RecordBuilder.create(instance -> instance.group(
            BlockStateProvider.CODEC.fieldOf("state_provider").forGetter(Config.class, Config::stateProvider),
            Codecs.INT.optionalFieldOf("radius", 3).forGetter(Config.class, Config::radius),
            Codecs.INT.optionalFieldOf("half_height", 1).forGetter(Config.class, Config::halfHeight),
            ExtraCodecs.BLOCK_INFO.list().optionalFieldOf("targets", Collections.emptyList()).forGetter(Config.class, Config::targets)
        ).apply(instance, Config::new)).describe("DiskConfig");
    }
}