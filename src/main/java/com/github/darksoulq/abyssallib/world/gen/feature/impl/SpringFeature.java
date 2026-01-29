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

public class SpringFeature extends Feature<SpringFeature.Config> {

    public SpringFeature() {
        super(Config.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        Location pos = context.origin();
        int x = pos.getBlockX();
        int y = pos.getBlockY();
        int z = pos.getBlockZ();

        if (context.level().getType(x, y, z) != Material.AIR && context.level().getType(x, y, z) != Material.CAVE_AIR) return false;
        if (context.config().requiresBlockBelow && context.level().getType(x, y - 1, z) == Material.AIR) return false;

        int solidNeighbors = 0;
        int openNeighbors = 0;

        int[][] dirs = {{1,0,0}, {-1,0,0}, {0,0,1}, {0,0,-1}, {0,1,0}, {0,-1,0}};
        for (int[] d : dirs) {
            Location check = pos.clone().add(d[0], d[1], d[2]);
            Material m = context.level().getType(check.getBlockX(), check.getBlockY(), check.getBlockZ());

            if (WorldGenUtils.isValidBlock(context.level(), check, context.config().validBlocks)) {
                solidNeighbors++;
            } else if (m == Material.AIR || m == Material.CAVE_AIR) {
                openNeighbors++;
            }
        }

        if (solidNeighbors == 5 && openNeighbors <= 1) {
            WorldGenUtils.placeBlock(context.level(), pos, context.config().fluid);
            return true;
        }
        return false;
    }

    public record Config(BlockInfo fluid, boolean requiresBlockBelow, List<String> validBlocks) implements FeatureConfig {
        public static final Codec<Config> CODEC = new Codec<>() {
            @Override
            public <D> Config decode(DynamicOps<D> ops, D input) throws CodecException {
                Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
                BlockInfo fluid = BlockStateCodec.CODEC.decode(ops, map.get(ops.createString("fluid")));
                boolean req = Codecs.BOOLEAN.decode(ops, map.get(ops.createString("requires_block_below")));
                List<String> valid = new ArrayList<>();
                if (map.containsKey(ops.createString("valid_blocks"))) {
                    valid = Codecs.STRING.list().decode(ops, map.get(ops.createString("valid_blocks")));
                }
                return new Config(fluid, req, valid);
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, Config value) throws CodecException {
                Map<D, D> map = new HashMap<>();
                map.put(ops.createString("fluid"), BlockStateCodec.CODEC.encode(ops, value.fluid));
                map.put(ops.createString("requires_block_below"), Codecs.BOOLEAN.encode(ops, value.requiresBlockBelow));
                map.put(ops.createString("valid_blocks"), Codecs.STRING.list().encode(ops, value.validBlocks));
                return ops.createMap(map);
            }
        };
    }
}