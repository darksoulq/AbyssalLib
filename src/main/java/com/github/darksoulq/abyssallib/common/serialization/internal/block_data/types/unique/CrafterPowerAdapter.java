package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types.unique;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Crafter;

public class CrafterPowerAdapter extends Adapter<Crafter> {
    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof Crafter;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, Crafter value) throws Codec.CodecException {
        return Codecs.BOOLEAN.encode(ops, value.isTriggered());
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, BlockData base) throws Codec.CodecException {
        if (!(base instanceof Crafter crafter)) return;
        boolean value = Codecs.BOOLEAN.decode(ops, input);
        crafter.setTriggered(value);
    }
}
