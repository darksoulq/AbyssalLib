package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.Attachable;
import org.bukkit.block.data.BlockData;

public class AttachableAdapter extends Adapter<Attachable> {
    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof Attachable;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, Attachable value) {
        return Codecs.BOOLEAN.encode(ops, value.isAttached());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, BlockData base) {
        if (!(base instanceof Attachable attachable))
            return DataResult.error(DataError.custom("Base is not Attachable, got: " + base.getClass().getSimpleName()));

        return Codecs.BOOLEAN.decode(ops, input).flatMap(value -> {
            attachable.setAttached(value);
            return DataResult.success(null);
        });
    }
}