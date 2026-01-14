package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.Axis;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Orientable;

public class OrientableAdapter extends Adapter<Orientable> {
    private static final Codec<Axis> CODEC = Codec.enumCodec(Axis.class);
    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof Orientable;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, Orientable value) throws Codec.CodecException {
        return CODEC.encode(ops, value.getAxis());
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, BlockData base) throws Codec.CodecException {
        if (!(base instanceof Orientable orientable)) return;
        Axis value = CODEC.decode(ops, input);
        if (!orientable.getAxes().contains(value)) return;
        orientable.setAxis(value);
    }
}
