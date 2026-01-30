package com.github.darksoulq.abyssallib.world.gen.feature.impl;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.gen.feature.Feature;
import com.github.darksoulq.abyssallib.world.gen.feature.FeatureConfig;
import com.github.darksoulq.abyssallib.world.gen.feature.FeaturePlaceContext;
import com.github.darksoulq.abyssallib.world.gen.feature.util.BlockStateCodec;
import com.github.darksoulq.abyssallib.world.gen.internal.WorldGenUtils;
import com.github.darksoulq.abyssallib.world.structure.processor.BlockInfo;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import java.util.*;

/**
 * A world generation feature that creates multi-layered geode structures.
 * <p>
 * The geode is generated using a distance-based algorithm distorted by simplex noise
 * to create an organic shape. It consists of an outer shell, a middle layer, an
 * inner layer, and a hollow or fluid-filled interior containing potential crystals.
 */
public class GeodeFeature extends Feature<GeodeFeature.Config> {

    /**
     * Constructs a new GeodeFeature with the associated configuration codec.
     */
    public GeodeFeature() {
        super(Config.CODEC);
    }

    /**
     * Executes the placement logic for a geode.
     * <p>
     * The process validates the origin, calculates a randomized radius, and iterates
     * through a cubic volume. For each point, it calculates a noise-distorted distance
     * from the center to determine which layer (outer, middle, inner, or hollow)
     * should be placed. Finally, it attempts to seed crystals on the inner surface.
     *
     * @param context The {@link FeaturePlaceContext} providing world access, origin, random source, and configuration.
     * @return {@code true} if the geode was successfully placed; {@code false} otherwise.
     */
    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        Location origin = context.origin();
        Config config = context.config();
        Random random = context.random();

        if (!WorldGenUtils.isValidBlock(context.level(), origin, config.invalidBlocks)) {
            return false;
        }

        int radius = config.outerRadius + random.nextInt(config.radiusRange);
        int innerRadius = radius - config.layerThickness;

        if (innerRadius < 2) return false;

        SimplexOctaveGenerator noise = new SimplexOctaveGenerator(new Random(context.level().getWorld().getSeed()), 1);
        noise.setScale(config.noiseScale);

        List<Vector> potentialCrystals = new ArrayList<>();

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    double dist = Math.sqrt(x * x + y * y + z * z);
                    if (dist > radius) continue;

                    Location current = origin.clone().add(x, y, z);
                    if (current.getY() < context.level().getWorld().getMinHeight() || current.getY() >= context.level().getWorld().getMaxHeight()) continue;

                    if (!WorldGenUtils.isValidBlock(context.level(), current, config.replaceable)) continue;

                    double n = noise.noise(current.getX(), current.getY(), current.getZ(), 0.5, 0.5);
                    double distortedDist = dist + (n * config.noiseMultiplier);

                    if (distortedDist <= innerRadius - 1) {
                        if (random.nextFloat() < config.fillChance) {
                            WorldGenUtils.placeBlock(context.level(), current, config.fillBlock);
                        } else {
                            WorldGenUtils.placeBlock(context.level(), current, config.innerBlock);
                            if (distortedDist > innerRadius - 2) {
                                potentialCrystals.add(new Vector(x, y, z));
                            }
                        }
                    } else if (distortedDist <= innerRadius) {
                        WorldGenUtils.placeBlock(context.level(), current, config.innerLayer);
                    } else if (distortedDist <= innerRadius + 1) {
                        WorldGenUtils.placeBlock(context.level(), current, config.middleLayer);
                    } else {
                        WorldGenUtils.placeBlock(context.level(), current, config.outerLayer);
                    }
                }
            }
        }

        for (Vector pos : potentialCrystals) {
            if (random.nextFloat() < config.crystalChance) {
                Location crystalLoc = origin.clone().add(pos);
                if (context.level().getType(crystalLoc.getBlockX(), crystalLoc.getBlockY(), crystalLoc.getBlockZ()) == Material.AIR) {
                    BlockInfo crystal = config.crystals.get(random.nextInt(config.crystals.size()));
                    WorldGenUtils.placeBlock(context.level(), crystalLoc, crystal);
                }
            }
        }

        return true;
    }

    /**
     * Configuration record for {@link GeodeFeature}.
     *
     * @param outerLayer      Block used for the outermost shell.
     * @param middleLayer     Block used for the middle shell.
     * @param innerLayer      Block used for the innermost shell.
     * @param fillBlock       Block used to fill the hollow center (e.g., Water or Air).
     * @param innerBlock      Block used for the hollow air/interior space.
     * @param crystals        List of potential crystal {@link BlockInfo}s to place inside.
     * @param replaceable     List of block IDs that the geode can overwrite.
     * @param invalidBlocks   List of block IDs that prevent the geode from starting at a location.
     * @param outerRadius     The base radius of the entire structure.
     * @param radiusRange     Random variance added to the base radius.
     * @param layerThickness  Thickness of the combined shells.
     * @param fillChance      Probability of a center block being replaced by {@code fillBlock}.
     * @param crystalChance   Probability of a crystal growing on a valid inner surface.
     * @param noiseScale      Scale of the simplex noise distortion.
     * @param noiseMultiplier Strength of the noise distortion.
     */
    public record Config(
        BlockInfo outerLayer,
        BlockInfo middleLayer,
        BlockInfo innerLayer,
        BlockInfo fillBlock,
        BlockInfo innerBlock,
        List<BlockInfo> crystals,
        List<String> replaceable,
        List<String> invalidBlocks,
        int outerRadius,
        int radiusRange,
        int layerThickness,
        double fillChance,
        double crystalChance,
        double noiseScale,
        double noiseMultiplier
    ) implements FeatureConfig {

        /**
         * The codec for serializing and deserializing the geode configuration.
         */
        public static final Codec<Config> CODEC = new Codec<>() {

            /**
             * Decodes the configuration from a complex map structure.
             *
             * @param ops   The dynamic operations logic.
             * @param input The serialized input.
             * @param <D>   The data format type.
             * @return A new {@link Config} instance.
             * @throws CodecException If required fields are missing or invalid.
             */
            @Override
            public <D> Config decode(DynamicOps<D> ops, D input) throws CodecException {
                Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));

                BlockInfo outer = BlockStateCodec.CODEC.decode(ops, map.get(ops.createString("outer_layer")));
                BlockInfo middle = BlockStateCodec.CODEC.decode(ops, map.get(ops.createString("middle_layer")));
                BlockInfo inner = BlockStateCodec.CODEC.decode(ops, map.get(ops.createString("inner_layer")));
                BlockInfo fill = BlockStateCodec.CODEC.decode(ops, map.get(ops.createString("fill_block")));
                BlockInfo inside = BlockStateCodec.CODEC.decode(ops, map.get(ops.createString("inner_block")));

                List<BlockInfo> crystals = BlockStateCodec.CODEC.list().decode(ops, map.get(ops.createString("crystals")));
                List<String> replaceable = Codecs.STRING.list().decode(ops, map.get(ops.createString("replaceable")));
                List<String> invalid = Codecs.STRING.list().decode(ops, map.get(ops.createString("invalid_blocks")));

                int r = Codecs.INT.decode(ops, map.get(ops.createString("outer_radius")));
                int range = Codecs.INT.decode(ops, map.get(ops.createString("radius_range")));
                int th = Codecs.INT.decode(ops, map.get(ops.createString("layer_thickness")));

                double fc = Codecs.DOUBLE.decode(ops, map.get(ops.createString("fill_chance")));
                double cc = Codecs.DOUBLE.decode(ops, map.get(ops.createString("crystal_chance")));
                double ns = Codecs.DOUBLE.decode(ops, map.get(ops.createString("noise_scale")));
                double nm = Codecs.DOUBLE.decode(ops, map.get(ops.createString("noise_multiplier")));

                return new Config(outer, middle, inner, fill, inside, crystals, replaceable, invalid, r, range, th, fc, cc, ns, nm);
            }

            /**
             * Encodes the configuration into a serialized map.
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
                map.put(ops.createString("outer_layer"), BlockStateCodec.CODEC.encode(ops, value.outerLayer));
                map.put(ops.createString("middle_layer"), BlockStateCodec.CODEC.encode(ops, value.middleLayer));
                map.put(ops.createString("inner_layer"), BlockStateCodec.CODEC.encode(ops, value.innerLayer));
                map.put(ops.createString("fill_block"), BlockStateCodec.CODEC.encode(ops, value.fillBlock));
                map.put(ops.createString("inner_block"), BlockStateCodec.CODEC.encode(ops, value.innerBlock));
                map.put(ops.createString("crystals"), BlockStateCodec.CODEC.list().encode(ops, value.crystals));
                map.put(ops.createString("replaceable"), Codecs.STRING.list().encode(ops, value.replaceable));
                map.put(ops.createString("invalid_blocks"), Codecs.STRING.list().encode(ops, value.invalidBlocks));
                map.put(ops.createString("outer_radius"), Codecs.INT.encode(ops, value.outerRadius));
                map.put(ops.createString("radius_range"), Codecs.INT.encode(ops, value.radiusRange));
                map.put(ops.createString("layer_thickness"), Codecs.INT.encode(ops, value.layerThickness));
                map.put(ops.createString("fill_chance"), Codecs.DOUBLE.encode(ops, value.fillChance));
                map.put(ops.createString("crystal_chance"), Codecs.DOUBLE.encode(ops, value.crystalChance));
                map.put(ops.createString("noise_scale"), Codecs.DOUBLE.encode(ops, value.noiseScale));
                map.put(ops.createString("noise_multiplier"), Codecs.DOUBLE.encode(ops, value.noiseMultiplier));
                return ops.createMap(map);
            }
        };
    }
}