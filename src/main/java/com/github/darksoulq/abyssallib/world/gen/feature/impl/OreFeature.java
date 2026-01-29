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
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class OreFeature extends Feature<OreFeature.Config> {

    public OreFeature() {
        super(Config.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        Random random = context.random();
        Vector origin = context.origin().toVector();
        int size = context.config().size;

        float f = random.nextFloat() * 3.1415927F;
        float f1 = (float)size / 8.0F;
        double d0 = origin.getX() + Math.sin(f) * f1;
        double d1 = origin.getX() - Math.sin(f) * f1;
        double d2 = origin.getZ() + Math.cos(f) * f1;
        double d3 = origin.getZ() - Math.cos(f) * f1;
        double d4 = origin.getY() + random.nextInt(3) - 2;
        double d5 = origin.getY() + random.nextInt(3) - 2;

        for(int l = 0; l < size; ++l) {
            float f2 = (float)l / (float)size;
            double d6 = d0 + (d1 - d0) * f2;
            double d7 = d4 + (d5 - d4) * f2;
            double d8 = d2 + (d3 - d2) * f2;
            double d9 = random.nextDouble() * size / 16.0D;
            double d10 = (Math.sin(3.1415927F * f2) + 1.0F) * d9 + 1.0D;
            double d11 = (Math.sin(3.1415927F * f2) + 1.0F) * d9 + 1.0D;
            int i1 = (int)Math.floor(d6 - d10 / 2.0D);
            int j1 = (int)Math.floor(d7 - d11 / 2.0D);
            int k1 = (int)Math.floor(d8 - d10 / 2.0D);
            int l1 = (int)Math.floor(d6 + d10 / 2.0D);
            int i2 = (int)Math.floor(d7 + d11 / 2.0D);
            int j2 = (int)Math.floor(d8 + d10 / 2.0D);

            for(int k2 = i1; k2 <= l1; ++k2) {
                double d12 = ((double)k2 + 0.5D - d6) / (d10 / 2.0D);
                if (d12 * d12 < 1.0D) {
                    for(int l2 = j1; l2 <= i2; ++l2) {
                        double d13 = ((double)l2 + 0.5D - d7) / (d11 / 2.0D);
                        if (d12 * d12 + d13 * d13 < 1.0D) {
                            for(int i3 = k1; i3 <= j2; ++i3) {
                                double d14 = ((double)i3 + 0.5D - d8) / (d10 / 2.0D);
                                if (d12 * d12 + d13 * d13 + d14 * d14 < 1.0D && context.level().getWorld().getMinHeight() <= l2 && l2 < context.level().getWorld().getMaxHeight()) {

                                    Location loc = new Location(context.level().getWorld(), k2, l2, i3);
                                    for (Target target : context.config().targets) {
                                        if (WorldGenUtils.isValidBlock(context.level(), loc, target.target)) {
                                            WorldGenUtils.placeBlock(context.level(), loc, target.state);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    public record Target(List<String> target, BlockInfo state) {
        public static final Codec<Target> CODEC = new Codec<>() {
            @Override
            public <D> Target decode(DynamicOps<D> ops, D input) throws CodecException {
                Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
                List<String> target = Codecs.STRING.list().decode(ops, map.get(ops.createString("target")));
                BlockInfo state = BlockStateCodec.CODEC.decode(ops, map.get(ops.createString("state")));
                return new Target(target, state);
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, Target value) throws CodecException {
                Map<D, D> map = new HashMap<>();
                map.put(ops.createString("target"), Codecs.STRING.list().encode(ops, value.target));
                map.put(ops.createString("state"), BlockStateCodec.CODEC.encode(ops, value.state));
                return ops.createMap(map);
            }
        };
    }

    public record Config(List<Target> targets, int size) implements FeatureConfig {
        public static final Codec<Config> CODEC = new Codec<>() {
            @Override
            public <D> Config decode(DynamicOps<D> ops, D input) throws CodecException {
                Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
                List<Target> targets = Target.CODEC.list().decode(ops, map.get(ops.createString("targets")));
                int size = Codecs.INT.decode(ops, map.get(ops.createString("size")));
                return new Config(targets, size);
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, Config value) throws CodecException {
                Map<D, D> map = new HashMap<>();
                map.put(ops.createString("targets"), Target.CODEC.list().encode(ops, value.targets));
                map.put(ops.createString("size"), Codecs.INT.encode(ops, value.size));
                return ops.createMap(map);
            }
        };
    }
}