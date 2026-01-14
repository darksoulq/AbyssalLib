package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Powerable;

public class PowerableAdapter extends Adapter<Powerable> {
    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof Powerable;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, Powerable value) throws Codec.CodecException {
        return Codecs.BOOLEAN.encode(ops, value.isPowered());
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, BlockData base) throws Codec.CodecException {
        if (!(base instanceof Powerable powerable)) return;
        boolean value = Codecs.BOOLEAN.decode(ops, input);
        powerable.setPowered(value);
    }
}
