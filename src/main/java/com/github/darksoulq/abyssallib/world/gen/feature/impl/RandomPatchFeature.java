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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RandomPatchFeature extends Feature<RandomPatchFeature.Config> {

    public RandomPatchFeature() {
        super(Config.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        Random random = context.random();
        int placed = 0;
        int tries = context.config().tries;
        int xzSpread = context.config().xzSpread;
        int ySpread = context.config().ySpread;
        BlockInfo toPlace = context.config().toPlace;

        for (int i = 0; i < tries; i++) {
            int x = context.origin().getBlockX() + random.nextInt(xzSpread + 1) - random.nextInt(xzSpread + 1);
            int z = context.origin().getBlockZ() + random.nextInt(xzSpread + 1) - random.nextInt(xzSpread + 1);
            int y = context.origin().getBlockY() + random.nextInt(ySpread + 1) - random.nextInt(ySpread + 1);

            if (y < context.level().getWorld().getMinHeight() || y >= context.level().getWorld().getMaxHeight()) continue;

            Location loc = new Location(context.level().getWorld(), x, y, z);
            Location groundLoc = loc.clone().add(0, -1, 0);

            if (context.level().getType(x, y, z) != Material.AIR) continue;

            if (context.config().placeOn != null && !context.config().placeOn.isEmpty()) {
                if (!WorldGenUtils.isValidBlock(context.level(), groundLoc, context.config().placeOn)) {
                    continue;
                }
            } else {
                if (!context.level().getType(x, y - 1, z).isSolid()) continue;
            }

            WorldGenUtils.placeBlock(context.level(), loc, toPlace);
            placed++;
        }
        return placed > 0;
    }

    public record Config(BlockInfo toPlace, int tries, int xzSpread, int ySpread, List<String> placeOn) implements FeatureConfig {
        public static final Codec<Config> CODEC = new Codec<>() {
            @Override
            public <D> Config decode(DynamicOps<D> ops, D input) throws CodecException {
                Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
                BlockInfo block = BlockStateCodec.CODEC.decode(ops, map.get(ops.createString("block")));
                int tries = Codecs.INT.decode(ops, map.get(ops.createString("tries")));
                int xz = Codecs.INT.decode(ops, map.get(ops.createString("xz_spread")));
                int y = Codecs.INT.decode(ops, map.get(ops.createString("y_spread")));
                List<String> placeOn = new ArrayList<>();
                if (map.containsKey(ops.createString("place_on"))) {
                    placeOn = Codecs.STRING.list().decode(ops, map.get(ops.createString("place_on")));
                }
                return new Config(block, tries, xz, y, placeOn);
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, Config value) throws CodecException {
                Map<D, D> map = new HashMap<>();
                map.put(ops.createString("block"), BlockStateCodec.CODEC.encode(ops, value.toPlace));
                map.put(ops.createString("tries"), Codecs.INT.encode(ops, value.tries));
                map.put(ops.createString("xz_spread"), Codecs.INT.encode(ops, value.xzSpread));
                map.put(ops.createString("y_spread"), Codecs.INT.encode(ops, value.ySpread));
                map.put(ops.createString("place_on"), Codecs.STRING.list().encode(ops, value.placeOn));
                return ops.createMap(map);
            }
        };
    }
}