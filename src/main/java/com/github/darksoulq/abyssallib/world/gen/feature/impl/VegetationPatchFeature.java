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
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class VegetationPatchFeature extends Feature<VegetationPatchFeature.Config> {

    public VegetationPatchFeature() {
        super(Config.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        Location origin = context.origin();
        Config config = context.config();
        Random random = context.random();

        if (!WorldGenUtils.isValidBlock(context.level(), origin, config.replaceable)) return false;

        int radiusX = config.radius + random.nextInt(config.radius + 1);
        int radiusZ = config.radius + random.nextInt(config.radius + 1);
        boolean placed = false;

        for (int x = -radiusX; x <= radiusX; x++) {
            for (int z = -radiusZ; z <= radiusZ; z++) {
                if (x * x + z * z <= radiusX * radiusZ) {
                    Location pos = origin.clone().add(x, 0, z);

                    if (context.level().getType(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ()) == Material.AIR) {
                        for (int i = 0; i < config.depth * 2; i++) {
                            pos.subtract(0, 1, 0);
                            if (WorldGenUtils.isValidBlock(context.level(), pos, config.replaceable)) {
                                break;
                            }
                        }
                    } else if (!WorldGenUtils.isValidBlock(context.level(), pos, config.replaceable)) {
                        for (int i = 0; i < config.depth * 2; i++) {
                            pos.add(0, 1, 0);
                            if (WorldGenUtils.isValidBlock(context.level(), pos, config.replaceable) &&
                                context.level().getType(pos.getBlockX(), pos.getBlockY() + 1, pos.getBlockZ()) == Material.AIR) {
                                break;
                            }
                        }
                    }

                    if (WorldGenUtils.isValidBlock(context.level(), pos, config.replaceable)) {
                        WorldGenUtils.placeBlock(context.level(), pos, config.ground);

                        Location vegPos = pos.clone().add(0, 1, 0);
                        if (context.level().getType(vegPos.getBlockX(), vegPos.getBlockY(), vegPos.getBlockZ()) == Material.AIR) {
                            if (random.nextFloat() < config.vegetationChance) {
                                WorldGenUtils.placeBlock(context.level(), vegPos, config.vegetation);
                            }
                        }

                        for (int d = 1; d < config.depth; d++) {
                            Location below = pos.clone().subtract(0, d, 0);
                            if (WorldGenUtils.isValidBlock(context.level(), below, config.replaceable)) {
                                WorldGenUtils.placeBlock(context.level(), below, config.ground);
                            }
                        }
                        placed = true;
                    }
                }
            }
        }
        return placed;
    }

    public record Config(List<String> replaceable, BlockInfo ground, BlockInfo vegetation, int radius, int depth, float vegetationChance) implements FeatureConfig {
        public static final Codec<Config> CODEC = new Codec<>() {
            @Override
            public <D> Config decode(DynamicOps<D> ops, D input) throws CodecException {
                Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
                List<String> rep = Codecs.STRING.list().decode(ops, map.get(ops.createString("replaceable")));
                BlockInfo g = BlockStateCodec.CODEC.decode(ops, map.get(ops.createString("ground")));
                BlockInfo v = BlockStateCodec.CODEC.decode(ops, map.get(ops.createString("vegetation")));
                int r = Codecs.INT.decode(ops, map.get(ops.createString("radius")));
                int d = Codecs.INT.decode(ops, map.get(ops.createString("depth")));
                float c = Codecs.FLOAT.decode(ops, map.get(ops.createString("vegetation_chance")));
                return new Config(rep, g, v, r, d, c);
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, Config value) throws CodecException {
                Map<D, D> map = new HashMap<>();
                map.put(ops.createString("replaceable"), Codecs.STRING.list().encode(ops, value.replaceable));
                map.put(ops.createString("ground"), BlockStateCodec.CODEC.encode(ops, value.ground));
                map.put(ops.createString("vegetation"), BlockStateCodec.CODEC.encode(ops, value.vegetation));
                map.put(ops.createString("radius"), Codecs.INT.encode(ops, value.radius));
                map.put(ops.createString("depth"), Codecs.INT.encode(ops, value.depth));
                map.put(ops.createString("vegetation_chance"), Codecs.FLOAT.encode(ops, value.vegetationChance));
                return ops.createMap(map);
            }
        };
    }
}