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

public class SimpleBlockFeature extends Feature<SimpleBlockFeature.Config> {

    public SimpleBlockFeature() {
        super(Config.CODEC);
    }

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

    public record Config(BlockInfo toPlace, List<String> replace) implements FeatureConfig {
        public static final Codec<Config> CODEC = new Codec<>() {
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