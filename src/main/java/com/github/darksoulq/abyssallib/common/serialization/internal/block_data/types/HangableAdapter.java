package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Hangable;

public class HangableAdapter extends Adapter<Hangable> {
    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof Hangable;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, Hangable value) throws Codec.CodecException {
        return Codecs.BOOLEAN.encode(ops, value.isHanging());
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, BlockData base) throws Codec.CodecException {
        if (!(base instanceof Hangable hangable)) return;
        boolean value = Codecs.BOOLEAN.decode(ops, input);
        hangable.setHanging(value);
    }
}
