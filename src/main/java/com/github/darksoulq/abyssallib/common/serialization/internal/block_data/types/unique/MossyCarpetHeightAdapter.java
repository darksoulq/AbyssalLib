package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types.unique;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import net.minecraft.world.level.block.MossyCarpetBlock;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.MossyCarpet;
import org.bukkit.block.data.type.Wall;
import org.bukkit.craftbukkit.block.CraftBlock;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MossyCarpetHeightAdapter extends Adapter<MossyCarpet> {
    private static final Set<BlockFace> APPLICABLE = MossyCarpetBlock.PROPERTY_BY_DIRECTION.keySet().stream().map(CraftBlock::notchToBlockFace).collect(Collectors.toSet());
    private static final Codec<Map<BlockFace, Wall.Height>> CODEC = Codec.map(Codec.enumCodec(BlockFace.class), Codec.enumCodec(Wall.Height.class));

    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof MossyCarpet;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, MossyCarpet value) throws Codec.CodecException {
        Map<BlockFace, Wall.Height> result = new HashMap<>();
        APPLICABLE.forEach(face -> {
            result.put(face, value.getHeight(face));
        });
        return CODEC.encode(ops, result);
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, BlockData base) throws Codec.CodecException {
        if (!(base instanceof MossyCarpet carpet)) return;
        Map<BlockFace, Wall.Height> value = CODEC.decode(ops, input);
        value.forEach(carpet::setHeight);
    }
}
