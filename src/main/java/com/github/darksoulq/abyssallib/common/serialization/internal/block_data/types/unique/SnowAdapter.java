package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types.unique;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Snow;

public class SnowAdapter extends Adapter<Snow> {
    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof Snow;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, Snow value) throws Codec.CodecException {
        return Codecs.INT.encode(ops, value.getLayers());
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, BlockData base) throws Codec.CodecException {
        if (!(base instanceof Snow snow)) return;
        int value = Codecs.INT.decode(ops, input);
        if (value < snow.getMinimumLayers() || value > snow.getMaximumLayers()) return;
        snow.setLayers(value);
    }
}
