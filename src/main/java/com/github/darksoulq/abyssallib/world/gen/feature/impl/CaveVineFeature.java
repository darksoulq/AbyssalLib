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

import java.util.HashMap;
import java.util.Map;

/**
 * A world generation feature that creates hanging vines from ceilings.
 * <p>
 * This feature generates a vertical column of blocks downward from an origin point.
 * It ensures the vine is attached to a solid ceiling and differentiates between
 * the "body" segments and the "tip" segment of the vine.
 */
public class CaveVineFeature extends Feature<CaveVineFeature.Config> {

    /**
     * Constructs a new CaveVineFeature with the associated configuration codec.
     */
    public CaveVineFeature() {
        super(Config.CODEC);
    }

    /**
     * Executes the placement logic for a hanging cave vine.
     * <p>
     * The process validates that the origin is air and the block above is solid.
     * It then determines a random length and iterates downward, placing body blocks
     * until the final iteration, where it places the tip block. Placement terminates
     * early if it hits a non-air block or the world's minimum height.
     *
     * @param context The {@link FeaturePlaceContext} providing world access, origin, random source, and configuration.
     * @return {@code true} if at least one block of the vine was successfully placed; {@code false} otherwise.
     */
    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        Location origin = context.origin();

        if (context.level().getType(origin.getBlockX(), origin.getBlockY(), origin.getBlockZ()) != Material.AIR) return false;

        Location up = origin.clone().add(0, 1, 0);
        if (!context.level().getType(up.getBlockX(), up.getBlockY(), up.getBlockZ()).isSolid()) return false;

        int length = context.random().nextInt(context.config().maxLength) + 1;

        for (int i = 0; i < length; i++) {
            Location pos = origin.clone().subtract(0, i, 0);
            if (pos.getY() < context.level().getWorld().getMinHeight()) break;

            if (context.level().getType(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ()) != Material.AIR) break;

            if (i == length - 1) {
                WorldGenUtils.placeBlock(context.level(), pos, context.config().tip);
            } else {
                WorldGenUtils.placeBlock(context.level(), pos, context.config().body);
            }
        }
        return true;
    }

    /**
     * Configuration record for {@link CaveVineFeature}.
     *
     * @param body      The {@link BlockInfo} representing the upper/middle segments of the vine.
     * @param tip       The {@link BlockInfo} representing the bottom-most segment of the vine.
     * @param maxLength The maximum possible vertical length of the vine.
     */
    public record Config(BlockInfo body, BlockInfo tip, int maxLength) implements FeatureConfig {

        /**
         * The codec for serializing and deserializing {@link Config}.
         */
        public static final Codec<Config> CODEC = new Codec<>() {

            /**
             * Decodes the configuration from a serialized map.
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
                BlockInfo body = BlockStateCodec.CODEC.decode(ops, map.get(ops.createString("body")));
                BlockInfo tip = BlockStateCodec.CODEC.decode(ops, map.get(ops.createString("tip")));
                int max = Codecs.INT.decode(ops, map.get(ops.createString("max_length")));
                return new Config(body, tip, max);
            }

            /**
             * Encodes the configuration into a serialized map.
             *
             * @param ops   The dynamic operations logic.
             * @param value The configuration instance to encode.
             * @param <D>   The data format type.
             * @return The encoded data object.
             * @throws CodecException If serialization fails.
             */
            @Override
            public <D> D encode(DynamicOps<D> ops, Config value) throws CodecException {
                Map<D, D> map = new HashMap<>();
                map.put(ops.createString("body"), BlockStateCodec.CODEC.encode(ops, value.body));
                map.put(ops.createString("tip"), BlockStateCodec.CODEC.encode(ops, value.tip));
                map.put(ops.createString("max_length"), Codecs.INT.encode(ops, value.maxLength));
                return ops.createMap(map);
            }
        };
    }
}