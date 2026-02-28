package com.github.darksoulq.abyssallib.world.gen.feature.impl;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.world.gen.feature.Feature;
import com.github.darksoulq.abyssallib.world.gen.feature.FeatureConfig;
import com.github.darksoulq.abyssallib.world.gen.feature.FeaturePlaceContext;
import com.github.darksoulq.abyssallib.world.gen.internal.WorldGenUtils;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A world generation feature that places a single isolated block.
 * <p>
 * This feature simply places the targeted block at the specified origin, with an
 * optional restriction to only replace certain existing block types.
 */
public class SimpleBlockFeature extends Feature<SimpleBlockFeature.Config> {

    /**
     * Constructs a new SimpleBlockFeature with the associated configuration codec.
     */
    public SimpleBlockFeature() {
        super(Config.CODEC);
    }

    /**
     * Executes the placement logic for the single block.
     *
     * @param context The {@link FeaturePlaceContext} providing world access, origin, and configuration.
     * @return {@code true} if the block was successfully placed; {@code false} if validation failed.
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
     * @param toPlace The {@link BlockInfo} representing the block state to be generated.
     * @param replace A {@link List} of block IDs allowed to be replaced by this block.
     */
    public record Config(BlockInfo toPlace, List<String> replace) implements FeatureConfig {

        /**
         * The codec for serializing and deserializing {@link Config}.
         */
        public static final Codec<Config> CODEC = new Codec<>() {

            @Override
            public <D> Config decode(DynamicOps<D> ops, D input) throws CodecException {
                Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
                BlockInfo block = ExtraCodecs.BLOCK_INFO.decode(ops, map.get(ops.createString("block")));
                List<String> replace = new ArrayList<>();
                if (map.containsKey(ops.createString("replace"))) {
                    replace = Codecs.STRING.list().decode(ops, map.get(ops.createString("replace")));
                }
                return new Config(block, replace);
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, Config value) throws CodecException {
                Map<D, D> map = new HashMap<>();
                map.put(ops.createString("block"), ExtraCodecs.BLOCK_INFO.encode(ops, value.toPlace));
                map.put(ops.createString("replace"), Codecs.STRING.list().encode(ops, value.replace));
                return ops.createMap(map);
            }
        };
    }
}