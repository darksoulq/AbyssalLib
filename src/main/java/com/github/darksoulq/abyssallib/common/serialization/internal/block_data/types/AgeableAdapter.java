package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;

public class AgeableAdapter extends Adapter<Ageable> {
    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof Ageable;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, Ageable value) {
        return Codecs.INT.encode(ops, value.getAge());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, BlockData base) {
        if (!(base instanceof Ageable ageable))
            return DataResult.error(DataError.custom("Base is not Ageable, got: " + base.getClass().getSimpleName()));

        return Codecs.INT.decode(ops, input).flatMap(value -> {
            if (value > ageable.getMaximumAge()) {
                return DataResult.error(DataError.custom("Age value (" + value + ") exceeds maximum (" + ageable.getMaximumAge() + ")"));
            }
            ageable.setAge(value);
            return DataResult.success(null);
        });
    }
}