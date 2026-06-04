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
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * A world generation feature that generates a concentric, layered geode structure.
 */
public class GeodeFeature extends Feature<GeodeFeature.Config> {

    /**
     * Constructs a new GeodeFeature with its associated configuration codec.
     */
    public GeodeFeature() {
        super(Config.CODEC);
    }

    /**
     * Executes the generation logic for the geode cluster.
     *
     * @param context The feature place context providing world access and configuration.
     * @return True if the geode was successfully generated.
     */
    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        Location origin = context.origin();
        Config config = context.config();
        Random random = context.random();

        if (!WorldGenUtils.isValidBlock(context.level(), origin, config.invalidBlocks())) {
            return false;
        }

        int minHeight = context.level().getWorld().getMinHeight();
        int maxHeight = context.level().getWorld().getMaxHeight();

        int nodeCount = random.nextInt(4) + 3;
        Vector[] nodes = new Vector[nodeCount];

        for (int i = 0; i < nodeCount; i++) {
            double angle = random.nextDouble() * Math.PI * 2.0;
            double elevation = random.nextDouble() * Math.PI;
            double distance = random.nextDouble() * config.maxRadius() * 0.5;

            double dx = Math.sin(elevation) * Math.cos(angle) * distance;
            double dy = Math.cos(elevation) * distance;
            double dz = Math.sin(elevation) * Math.sin(angle) * distance;

            nodes[i] = new Vector(origin.getBlockX() + dx, origin.getBlockY() + dy, origin.getBlockZ() + dz);
        }

        int bounds = config.maxRadius() + 2;

        for (int x = origin.getBlockX() - bounds; x <= origin.getBlockX() + bounds; x++) {
            for (int y = origin.getBlockY() - bounds; y <= origin.getBlockY() + bounds; y++) {
                if (y < minHeight || y >= maxHeight) continue;

                for (int z = origin.getBlockZ() - bounds; z <= origin.getBlockZ() + bounds; z++) {
                    double minDistance = Double.MAX_VALUE;

                    for (Vector node : nodes) {
                        double distSq = Math.pow(x - node.getX(), 2) + Math.pow(y - node.getY(), 2) + Math.pow(z - node.getZ(), 2);
                        if (distSq < minDistance) {
                            minDistance = distSq;
                        }
                    }

                    double distance = Math.sqrt(minDistance);
                    Location currentLoc = new Location(context.level().getWorld(), x, y, z);

                    if (distance > config.maxRadius()) continue;

                    if (distance <= config.minRadius() - 1.5) {
                        BlockInfo fill = config.fillingProvider().getState(random, currentLoc);
                        if (fill != null) WorldGenUtils.placeBlock(context.level(), currentLoc, fill);
                    } else if (distance <= config.minRadius() - 0.5) {
                        if (random.nextDouble() < 0.1 && config.innerPlacementsProvider() != null) {
                            BlockInfo placement = config.innerPlacementsProvider().getState(random, currentLoc);
                            if (placement != null) {
                                WorldGenUtils.placeBlock(context.level(), currentLoc, placement);
                            }
                        } else {
                            BlockInfo inner = config.innerWallProvider().getState(random, currentLoc);
                            if (inner != null) WorldGenUtils.placeBlock(context.level(), currentLoc, inner);
                        }
                    } else if (distance <= config.minRadius() + 0.5) {
                        BlockInfo middle = config.middleWallProvider().getState(random, currentLoc);
                        if (middle != null) WorldGenUtils.placeBlock(context.level(), currentLoc, middle);
                    } else if (distance <= config.maxRadius()) {
                        BlockInfo outer = config.outerWallProvider().getState(random, currentLoc);
                        if (outer != null) WorldGenUtils.placeBlock(context.level(), currentLoc, outer);
                    }
                }
            }
        }

        return true;
    }

    /**
     * Configuration record for the geode feature.
     *
     * @param outerWallProvider        The block provider for the outermost protective shell.
     * @param middleWallProvider       The block provider for the intermediate transition layer.
     * @param innerWallProvider        The block provider for the inner geode crust.
     * @param fillingProvider          The block provider used to fill the hollow center.
     * @param innerPlacementsProvider  The block provider used for random crystal attachments on the inner wall.
     * @param invalidBlocks            A list of block info structures that will abort generation if encountered at the origin.
     * @param minRadius                The baseline radius of the inner geode cavity.
     * @param maxRadius                The absolute maximum radius of the geode's outermost shell.
     */
    public record Config(
        BlockStateProvider outerWallProvider,
        BlockStateProvider middleWallProvider,
        BlockStateProvider innerWallProvider,
        BlockStateProvider fillingProvider,
        BlockStateProvider innerPlacementsProvider,
        List<BlockInfo> invalidBlocks,
        int minRadius,
        int maxRadius
    ) implements FeatureConfig {

        /**
         * The codec for serializing and deserializing the configuration.
         */
        public static final Codec<Config> CODEC = RecordBuilder.create(instance -> instance.group(
            BlockStateProvider.CODEC.fieldOf("outer_wall_provider").forGetter(Config.class, Config::outerWallProvider),
            BlockStateProvider.CODEC.fieldOf("middle_wall_provider").forGetter(Config.class, Config::middleWallProvider),
            BlockStateProvider.CODEC.fieldOf("inner_wall_provider").forGetter(Config.class, Config::innerWallProvider),
            BlockStateProvider.CODEC.fieldOf("filling_provider").forGetter(Config.class, Config::fillingProvider),
            BlockStateProvider.CODEC.nullable().optionalFieldOf("inner_placements_provider", null).forGetter(Config.class, Config::innerPlacementsProvider),
            ExtraCodecs.BLOCK_INFO.list().optionalFieldOf("invalid_blocks", Collections.emptyList()).forGetter(Config.class, Config::invalidBlocks),
            Codecs.INT.optionalFieldOf("min_radius", 3).forGetter(Config.class, Config::minRadius),
            Codecs.INT.optionalFieldOf("max_radius", 5).forGetter(Config.class, Config::maxRadius)
        ).apply(instance, Config::new)).describe("GeodeConfig");
    }
}