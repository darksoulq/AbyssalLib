package com.github.darksoulq.abyssallib.world.structure.processor.impl;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.block.CustomBlock;
import com.github.darksoulq.abyssallib.world.gen.WorldGenAccess;
import com.github.darksoulq.abyssallib.world.structure.processor.BlockInfo;
import com.github.darksoulq.abyssallib.world.structure.processor.StructureProcessor;
import com.github.darksoulq.abyssallib.world.structure.processor.StructureProcessorType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockIgnoreProcessor extends StructureProcessor {
    public static final Codec<BlockIgnoreProcessor> CODEC = new Codec<>() {
        @Override
        public <D> BlockIgnoreProcessor decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            D listNode = map.get(ops.createString("blocks"));
            List<String> blocks = new ArrayList<>();
            if (listNode != null) {
                blocks = Codecs.STRING.list().decode(ops, listNode);
            }
            return new BlockIgnoreProcessor(blocks);
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, BlockIgnoreProcessor value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("blocks"), Codecs.STRING.list().encode(ops, value.ignoredIds));
            return ops.createMap(map);
        }
    };
    public static final StructureProcessorType<BlockIgnoreProcessor> TYPE = () -> CODEC;
    
    private final List<String> ignoredIds;

    public BlockIgnoreProcessor(List<String> ignoredIds) {
        this.ignoredIds = ignoredIds;
    }

    @Override
    public BlockInfo process(World world, Location origin, BlockInfo current, BlockInfo original) {
        if (shouldIgnore(current)) return null;
        return current;
    }

    @Override
    public BlockInfo process(WorldGenAccess level, Location origin, BlockInfo current, BlockInfo original) {
        if (shouldIgnore(current)) return null;
        return current;
    }

    private boolean shouldIgnore(BlockInfo current) {
        String id;
        if (current.block() instanceof CustomBlock cb) {
            id = cb.getId().toString();
        } else if (current.block() instanceof BlockData bd) {
            id = "minecraft:" + bd.getMaterial().name().toLowerCase();
        } else {
            return false;
        }
        return ignoredIds.contains(id);
    }

    @Override
    public StructureProcessorType<?> getType() {
        return TYPE;
    }
}