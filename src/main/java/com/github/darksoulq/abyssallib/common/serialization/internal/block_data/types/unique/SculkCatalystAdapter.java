package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types.unique;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.SculkCatalyst;

public class SculkCatalystAdapter extends Adapter<SculkCatalyst> {
    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof SculkCatalyst;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, SculkCatalyst value) throws Codec.CodecException {
        return Codecs.BOOLEAN.encode(ops, value.isBloom());
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, BlockData base) throws Codec.CodecException {
        if (!(base instanceof SculkCatalyst sculkCatalyst)) return;
        boolean value = Codecs.BOOLEAN.decode(ops, input);
        sculkCatalyst.setBloom(value);
    }
}
