package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types.unique;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.CaveVinesPlant;

public class CaveVinesPlantAdapter extends Adapter<CaveVinesPlant> {
    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof CaveVinesPlant;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, CaveVinesPlant value) throws Codec.CodecException {
        return Codecs.BOOLEAN.encode(ops, value.hasBerries());
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, BlockData base) throws Codec.CodecException {
        if (!(base instanceof CaveVinesPlant caveVinesPlant)) return;
        boolean value = Codecs.BOOLEAN.decode(ops, input);
        caveVinesPlant.setBerries(value);
    }
}
