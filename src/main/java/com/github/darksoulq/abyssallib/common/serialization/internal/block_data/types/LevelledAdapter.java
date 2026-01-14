package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;

public class LevelledAdapter extends Adapter<Levelled> {
    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof Levelled;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, Levelled value) throws Codec.CodecException {
        return Codecs.INT.encode(ops, value.getLevel());
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, BlockData base) throws Codec.CodecException {
        if (!(base instanceof Levelled levelled)) return;
        int value = Codecs.INT.decode(ops, input);
        if (value > levelled.getMaximumLevel()) return;
        levelled.setLevel(value);
    }
}
