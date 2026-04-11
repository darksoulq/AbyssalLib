package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types.unique;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Leaves;

public class LeavesPersistentAdapter extends Adapter<Leaves> {
    
    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof Leaves;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, Leaves value) throws Codec.CodecException {
        return Codecs.BOOLEAN.encode(ops, value.isPersistent());
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, BlockData base) throws Codec.CodecException {
        if (!(base instanceof Leaves leaves)) return;
        boolean value = Codecs.BOOLEAN.decode(ops, input);
        leaves.setPersistent(value);
    }
}