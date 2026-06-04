package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types.unique;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Crafter;

public class CrafterStateAdapter extends Adapter<Crafter> {
    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof Crafter;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, Crafter value) {
        return Codecs.BOOLEAN.encode(ops, value.isCrafting());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, BlockData base) {
        if (!(base instanceof Crafter crafter))
            return DataResult.error(DataError.custom("Base is not Crafter, got: " + base.getClass().getSimpleName()));

        return Codecs.BOOLEAN.decode(ops, input).flatMap(value -> {
            crafter.setCrafting(value);
            return DataResult.success(null);
        });
    }
}