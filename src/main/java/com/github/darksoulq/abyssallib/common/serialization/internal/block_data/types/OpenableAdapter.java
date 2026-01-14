package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Openable;

public class OpenableAdapter extends Adapter<Openable> {
    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof Openable;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, Openable value) throws Codec.CodecException {
        return Codecs.BOOLEAN.encode(ops, value.isOpen());
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, BlockData base) throws Codec.CodecException {
        if (!(base instanceof Openable openable)) return;
        boolean value = Codecs.BOOLEAN.decode(ops, input);
        openable.setOpen(value);
    }
}
