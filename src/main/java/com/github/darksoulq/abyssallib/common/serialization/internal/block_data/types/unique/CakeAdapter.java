package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types.unique;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Cake;

public class CakeAdapter extends Adapter<Cake> {
    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof Cake;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, Cake value) {
        return Codecs.INT.encode(ops, value.getBites());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, BlockData base) {
        if (!(base instanceof Cake cake))
            return DataResult.error(DataError.custom("Base is not Cake, got: " + base.getClass().getSimpleName()));

        return Codecs.INT.decode(ops, input).flatMap(value -> {
            if (value > cake.getMaximumBites()) {
                return DataResult.error(DataError.custom("Bites value (" + value + ") exceeds maximum (" + cake.getMaximumBites() + ")"));
            }
            cake.setBites(value);
            return DataResult.success(null);
        });
    }
}