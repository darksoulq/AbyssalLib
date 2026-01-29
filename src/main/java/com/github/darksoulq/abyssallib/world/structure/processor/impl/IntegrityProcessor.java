package com.github.darksoulq.abyssallib.world.structure.processor.impl;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.gen.WorldGenAccess;
import com.github.darksoulq.abyssallib.world.structure.processor.BlockInfo;
import com.github.darksoulq.abyssallib.world.structure.processor.StructureProcessor;
import com.github.darksoulq.abyssallib.world.structure.processor.StructureProcessorType;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class IntegrityProcessor extends StructureProcessor {
    public static final Codec<IntegrityProcessor> CODEC = new Codec<>() {
        @Override
        public <D> IntegrityProcessor decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            D val = map.get(ops.createString("integrity"));
            float integrity = 1.0f;
            if (val != null) {
                integrity = Codecs.FLOAT.decode(ops, val);
            }
            return new IntegrityProcessor(integrity);
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, IntegrityProcessor value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("integrity"), Codecs.FLOAT.encode(ops, value.integrity));
            return ops.createMap(map);
        }
    };

    public static final StructureProcessorType<IntegrityProcessor> TYPE = () -> CODEC;

    private final float integrity;
    private final Random random = new Random();

    public IntegrityProcessor(float integrity) {
        this.integrity = integrity;
    }

    @Override
    public BlockInfo process(World world, Location origin, BlockInfo current, BlockInfo original) {
        if (integrity >= 1.0f) return current;
        return random.nextFloat() <= integrity ? current : null;
    }

    @Override
    public BlockInfo process(WorldGenAccess level, Location origin, BlockInfo current, BlockInfo original) {
        if (integrity >= 1.0f) return current;
        return random.nextFloat() <= integrity ? current : null;
    }

    @Override
    public StructureProcessorType<?> getType() {
        return TYPE;
    }
}