package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.AnaloguePowerable;
import org.bukkit.block.data.BlockData;

public class AnaloguePowerableAdapter extends Adapter<AnaloguePowerable> {
    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof AnaloguePowerable;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, AnaloguePowerable value) throws Codec.CodecException {
        return Codecs.INT.encode(ops, value.getPower());
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, BlockData base) throws Codec.CodecException {
        if (!(base instanceof AnaloguePowerable powerable)) return;
        int value = Codecs.INT.decode(ops, input);
        if (value > powerable.getMaximumPower()) return;
        powerable.setPower(value);
    }
}
