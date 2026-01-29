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

public class CaveVineFeature extends Feature<CaveVineFeature.Config> {

    public CaveVineFeature() {
        super(Config.CODEC);
    }

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

    public record Config(BlockInfo body, BlockInfo tip, int maxLength) implements FeatureConfig {
        public static final Codec<Config> CODEC = new Codec<>() {
            @Override
            public <D> Config decode(DynamicOps<D> ops, D input) throws CodecException {
                Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
                BlockInfo body = BlockStateCodec.CODEC.decode(ops, map.get(ops.createString("body")));
                BlockInfo tip = BlockStateCodec.CODEC.decode(ops, map.get(ops.createString("tip")));
                int max = Codecs.INT.decode(ops, map.get(ops.createString("max_length")));
                return new Config(body, tip, max);
            }

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