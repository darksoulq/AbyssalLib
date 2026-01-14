package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;

public class BisectedAdapter extends Adapter<Bisected> {
    private static final Codec<Bisected.Half> CODEC = Codec.enumCodec(Bisected.Half.class);
    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof Bisected;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, Bisected value) throws Codec.CodecException {
        return CODEC.encode(ops, value.getHalf());
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, BlockData base) throws Codec.CodecException {
        if (!(base instanceof Bisected bisected)) return;
        Bisected.Half value = CODEC.decode(ops, input);
        bisected.setHalf(value);
    }
}
