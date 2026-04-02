package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types.unique;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Bed;

public class BedPartAdapter extends Adapter<Bed> {
    private static final Codec<Bed.Part> CODEC = Codec.enumCodec(Bed.Part.class);
    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof Bed;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, Bed value) throws Codec.CodecException {
        return CODEC.encode(ops, value.getPart());
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, BlockData base) throws Codec.CodecException {
        if (!(base instanceof Bed bed)) return;
        Bed.Part part = CODEC.decode(ops, input);
        bed.setPart(part);
    }
}
