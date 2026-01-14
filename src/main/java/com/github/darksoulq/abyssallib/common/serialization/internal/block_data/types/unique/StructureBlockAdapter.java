package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types.unique;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.StructureBlock;

public class StructureBlockAdapter extends Adapter<StructureBlock> {
    private static final Codec<StructureBlock.Mode> CODEC = Codec.enumCodec(StructureBlock.Mode.class);

    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof StructureBlock;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, StructureBlock value) throws Codec.CodecException {
        return CODEC.encode(ops, value.getMode());
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, BlockData base) throws Codec.CodecException {
        if (!(base instanceof StructureBlock structureBlock)) return;
        StructureBlock.Mode value = CODEC.decode(ops, input);
        structureBlock.setMode(value);
    }
}
