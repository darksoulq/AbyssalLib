package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types.unique;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Cake;

public class CakeAdapter extends Adapter<Cake> {
    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof Cake;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, Cake value) throws Codec.CodecException {
        return Codecs.INT.encode(ops, value.getBites());
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, BlockData base) throws Codec.CodecException {
        if (!(base instanceof Cake cake)) return;
        int value = Codecs.INT.decode(ops, input);
        if (value > cake.getMaximumBites()) return;
        cake.setBites(value);
    }
}
