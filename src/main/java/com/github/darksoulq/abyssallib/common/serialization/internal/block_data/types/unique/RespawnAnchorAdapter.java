package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types.unique;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.RespawnAnchor;

public class RespawnAnchorAdapter extends Adapter<RespawnAnchor> {
    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof RespawnAnchor;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, RespawnAnchor value) throws Codec.CodecException {
        return Codecs.INT.encode(ops, value.getCharges());
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, BlockData base) throws Codec.CodecException {
        if (!(base instanceof RespawnAnchor respawnAnchor)) return;
        int value = Codecs.INT.decode(ops, input);
        if (value > respawnAnchor.getMaximumCharges()) return;
        respawnAnchor.setCharges(value);
    }
}
