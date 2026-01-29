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

public class DiskFeature extends Feature<DiskFeature.Config> {

    public DiskFeature() {
        super(Config.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        boolean placed = false;
        int radius = context.config().radius;
        int ySize = context.config().halfHeight;
        Location origin = context.origin();

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z <= radius * radius) {
                    for (int y = -ySize; y <= ySize; y++) {
                        Location loc = origin.clone().add(x, y, z);
                        if (WorldGenUtils.isValidBlock(context.level(), loc, context.config().targets)) {
                            WorldGenUtils.placeBlock(context.level(), loc, context.config().toPlace);
                            placed = true;
                        }
                    }
                }
            }
        }
        return placed;
    }

    public record Config(BlockInfo toPlace, int radius, int halfHeight, List<String> targets) implements FeatureConfig {
        public static final Codec<Config> CODEC = new Codec<>() {
            @Override
            public <D> Config decode(DynamicOps<D> ops, D input) throws CodecException {
                Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
                BlockInfo block = BlockStateCodec.CODEC.decode(ops, map.get(ops.createString("block")));
                int r = Codecs.INT.decode(ops, map.get(ops.createString("radius")));
                int h = Codecs.INT.decode(ops, map.get(ops.createString("half_height")));

                List<String> targets = new ArrayList<>();
                if (map.containsKey(ops.createString("targets"))) {
                    targets = Codecs.STRING.list().decode(ops, map.get(ops.createString("targets")));
                }
                return new Config(block, r, h, targets);
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, Config value) throws CodecException {
                Map<D, D> map = new HashMap<>();
                map.put(ops.createString("block"), BlockStateCodec.CODEC.encode(ops, value.toPlace));
                map.put(ops.createString("radius"), Codecs.INT.encode(ops, value.radius));
                map.put(ops.createString("half_height"), Codecs.INT.encode(ops, value.halfHeight));
                map.put(ops.createString("targets"), Codecs.STRING.list().encode(ops, value.targets));
                return ops.createMap(map);
            }
        };
    }
}