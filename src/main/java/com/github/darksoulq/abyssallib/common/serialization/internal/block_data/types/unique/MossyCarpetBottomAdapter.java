package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types.unique;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.MossyCarpet;

public class MossyCarpetBottomAdapter extends Adapter<MossyCarpet> {
    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof MossyCarpet;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, MossyCarpet value) {
        return Codecs.BOOLEAN.encode(ops, value.isBottom());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, BlockData base) {
        if (!(base instanceof MossyCarpet carpet))
            return DataResult.error(DataError.custom("Base is not MossyCarpet, got: " + base.getClass().getSimpleName()));

        return Codecs.BOOLEAN.decode(ops, input).flatMap(value -> {
            carpet.setBottom(value);
            return DataResult.success(null);
        });
    }
}