package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Hatchable;

public class HatchableAdapter extends Adapter<Hatchable> {
    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof Hatchable;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, Hatchable value) throws Codec.CodecException {
        return Codecs.INT.encode(ops, value.getHatch());
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, BlockData base) throws Codec.CodecException {
        if (!(base instanceof Hatchable hatchable)) return;
        int value = Codecs.INT.decode(ops, input);
        if (value > hatchable.getMaximumHatch()) return;
        hatchable.setHatch(value);
    }
}
