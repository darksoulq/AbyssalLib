package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types.unique;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.Orientation;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Crafter;

public class CrafterOrientationAdapter extends Adapter<Crafter> {
    private static final Codec<Orientation> CODEC = Codec.enumCodec(Orientation.class);

    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof Crafter;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, Crafter value) throws Codec.CodecException {
        return CODEC.encode(ops, value.getOrientation());
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, BlockData base) throws Codec.CodecException {
        if (!(base instanceof Crafter crafter)) return;
        Orientation value = CODEC.decode(ops, input);
        crafter.setOrientation(value);
    }
}
