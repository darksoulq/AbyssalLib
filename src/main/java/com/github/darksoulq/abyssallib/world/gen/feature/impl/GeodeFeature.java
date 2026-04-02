package com.github.darksoulq.abyssallib.world.gen.feature.impl;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.world.gen.feature.Feature;
import com.github.darksoulq.abyssallib.world.gen.feature.FeatureConfig;
import com.github.darksoulq.abyssallib.world.gen.feature.FeaturePlaceContext;
import com.github.darksoulq.abyssallib.world.gen.internal.WorldGenUtils;
import com.github.darksoulq.abyssallib.world.gen.state.provider.BlockStateProvider;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

                BlockStateProvider outerWall = BlockStateProvider.CODEC.decode(ops, map.get(ops.createString("outer_wall_provider")));
                BlockStateProvider middleWall = BlockStateProvider.CODEC.decode(ops, map.get(ops.createString("middle_wall_provider")));
                BlockStateProvider innerWall = BlockStateProvider.CODEC.decode(ops, map.get(ops.createString("inner_wall_provider")));
                BlockStateProvider filling = BlockStateProvider.CODEC.decode(ops, map.get(ops.createString("filling_provider")));
                
                BlockStateProvider innerPlacements = null;
                D innerPlacementsNode = map.get(ops.createString("inner_placements_provider"));
                if (innerPlacementsNode != null) {
                    innerPlacements = BlockStateProvider.CODEC.decode(ops, innerPlacementsNode);
                }

                List<BlockInfo> invalidBlocks = ExtraCodecs.BLOCK_INFO.list().decode(ops, map.get(ops.createString("invalid_blocks")));
                
                int minRadius = Codecs.INT.decode(ops, map.get(ops.createString("min_radius")));
                int maxRadius = Codecs.INT.decode(ops, map.get(ops.createString("max_radius")));

                return new Config(outerWall, middleWall, innerWall, filling, innerPlacements, invalidBlocks, minRadius, maxRadius);
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

                map.put(ops.createString("outer_wall_provider"), BlockStateProvider.CODEC.encode(ops, value.outerWallProvider));
                map.put(ops.createString("middle_wall_provider"), BlockStateProvider.CODEC.encode(ops, value.middleWallProvider));
                map.put(ops.createString("inner_wall_provider"), BlockStateProvider.CODEC.encode(ops, value.innerWallProvider));
                map.put(ops.createString("filling_provider"), BlockStateProvider.CODEC.encode(ops, value.fillingProvider));
                
                if (value.innerPlacementsProvider != null) {
                    map.put(ops.createString("inner_placements_provider"), BlockStateProvider.CODEC.encode(ops, value.innerPlacementsProvider));
                }

                map.put(ops.createString("invalid_blocks"), ExtraCodecs.BLOCK_INFO.list().encode(ops, value.invalidBlocks));
                
                map.put(ops.createString("min_radius"), Codecs.INT.encode(ops, value.minRadius));
                map.put(ops.createString("max_radius"), Codecs.INT.encode(ops, value.maxRadius));

                return ops.createMap(map);
            }
        };
    }
}