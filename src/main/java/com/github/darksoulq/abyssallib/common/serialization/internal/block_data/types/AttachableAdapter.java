package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.Attachable;
import org.bukkit.block.data.BlockData;

public class AttachableAdapter extends Adapter<Attachable> {
    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof Attachable;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, Attachable value) throws Codec.CodecException {
        return Codecs.BOOLEAN.encode(ops, value.isAttached());
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, BlockData base) throws Codec.CodecException {
        if (!(base instanceof Attachable attachable)) return;
        boolean value = Codecs.BOOLEAN.decode(ops, input);
        attachable.setAttached(value);
    }
}
