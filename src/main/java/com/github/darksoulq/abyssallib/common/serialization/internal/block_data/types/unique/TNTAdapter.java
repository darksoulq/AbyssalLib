package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types.unique;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.TNT;

public class TNTAdapter extends Adapter<TNT> {
    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof TNT;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, TNT value) {
        return Codecs.BOOLEAN.encode(ops, value.isUnstable());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, BlockData base) {
        if (!(base instanceof TNT tnt))
            return DataResult.error(DataError.custom("Base is not TNT, got: " + base.getClass().getSimpleName()));

        return Codecs.BOOLEAN.decode(ops, input).flatMap(value -> {
            tnt.setUnstable(value);
            return DataResult.success(null);
        });
    }
}