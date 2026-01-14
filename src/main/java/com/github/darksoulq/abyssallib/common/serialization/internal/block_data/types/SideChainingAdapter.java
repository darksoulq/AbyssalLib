package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.SideChaining;

public class SideChainingAdapter extends Adapter<SideChaining> {
    private static final Codec<SideChaining.ChainPart> CODEC = Codec.enumCodec(SideChaining.ChainPart.class);
    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof SideChaining;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, SideChaining value) throws Codec.CodecException {
        return CODEC.encode(ops, value.getSideChain());
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, BlockData base) throws Codec.CodecException {
        if (!(base instanceof SideChaining sideChaining)) return;
        SideChaining.ChainPart value = CODEC.decode(ops, input);
        sideChaining.setSideChain(value);
    }
}
