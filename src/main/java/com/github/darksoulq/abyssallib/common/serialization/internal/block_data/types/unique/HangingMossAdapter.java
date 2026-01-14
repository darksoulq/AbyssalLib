package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types.unique;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.HangingMoss;

public class HangingMossAdapter extends Adapter<HangingMoss> {
    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof HangingMoss;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, HangingMoss value) throws Codec.CodecException {
        return Codecs.BOOLEAN.encode(ops, value.isTip());
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, BlockData base) throws Codec.CodecException {
        if (!(base instanceof HangingMoss hangingMoss)) return;
        boolean value = Codecs.BOOLEAN.decode(ops, input);
        hangingMoss.setTip(value);
    }
}
