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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A world generation feature that places a single block at the origin location.
 * <p>
 * This feature provides a simple mechanism for single-block placement, with the
 * ability to restrict placement to specific replaceable block types.
 */
public class SimpleBlockFeature extends Feature<SimpleBlockFeature.Config> {

    /**
     * Constructs a new SimpleBlockFeature with the associated configuration codec.
     */
    public SimpleBlockFeature() {
        super(Config.CODEC);
    }

    /**
     * Executes the placement of the single block.
     * <p>
     * The method first checks if the "replace" list is non-empty. If so, it validates
     * that the block at the origin location is allowed to be replaced. If validation
     * passes, the configured block state is placed.
     *
     * @param context The {@link FeaturePlaceContext} providing world access, origin, and configuration.
     * @return {@code true} if the block was successfully placed; {@code false} if replacement validation failed.
     */
    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        Location origin = context.origin();

        if (!context.config().replace.isEmpty()) {
            if (!WorldGenUtils.isValidBlock(context.level(), origin, context.config().replace)) {
                return false;
            }
        }

        WorldGenUtils.placeBlock(context.level(), origin, context.config().toPlace);
        return true;
    }

    /**
     * Configuration record for {@link SimpleBlockFeature}.
     *
     * @param toPlace The {@link BlockInfo} representing the block state to be placed.
     * @param replace A {@link List} of block identifiers that are valid to be replaced.
     */
    public record Config(BlockInfo toPlace, List<String> replace) implements FeatureConfig {

        /**
         * The codec for serializing and deserializing the {@link Config}.
         */
        public static final Codec<Config> CODEC = new Codec<>() {

            /**
             * Decodes the configuration from a map structure.
             *
             * @param ops   The dynamic operations logic.
             * @param input The serialized input.
             * @param <D>   The data format type.
             * @return A new {@link Config} instance.
             * @throws CodecException If the "block" field is missing or invalid.
             */
            @Override
            public <D> Config decode(DynamicOps<D> ops, D input) throws CodecException {
                Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
                BlockInfo block = BlockStateCodec.CODEC.decode(ops, map.get(ops.createString("block")));
                List<String> replace = new ArrayList<>();
                if (map.containsKey(ops.createString("replace"))) {
                    replace = Codecs.STRING.list().decode(ops, map.get(ops.createString("replace")));
                }
                return new Config(block, replace);
            }

            /**
             * Encodes the configuration into a map structure.
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
                map.put(ops.createString("block"), BlockStateCodec.CODEC.encode(ops, value.toPlace));
                map.put(ops.createString("replace"), Codecs.STRING.list().encode(ops, value.replace));
                return ops.createMap(map);
            }
        };
    }
}