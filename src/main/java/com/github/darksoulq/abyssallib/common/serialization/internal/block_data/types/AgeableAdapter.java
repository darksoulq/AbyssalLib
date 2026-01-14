package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;

public class AgeableAdapter extends Adapter<Ageable> {
    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof Ageable;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, Ageable value) throws Codec.CodecException {
        return Codecs.INT.encode(ops, value.getAge());
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, BlockData base) throws Codec.CodecException {
        if (!(base instanceof Ageable ageable)) return;
        int value = Codecs.INT.decode(ops, input);
        if (value > ageable.getMaximumAge()) return;
        ageable.setAge(value);
    }
}
