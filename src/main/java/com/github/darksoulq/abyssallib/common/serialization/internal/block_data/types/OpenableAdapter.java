package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Openable;

public class OpenableAdapter extends Adapter<Openable> {
    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof Openable;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, Openable value) {
        return Codecs.BOOLEAN.encode(ops, value.isOpen());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, BlockData base) {
        if (!(base instanceof Openable openable))
            return DataResult.error(DataError.custom("Base is not Openable, got: " + base.getClass().getSimpleName()));
        return Codecs.BOOLEAN.decode(ops, input).flatMap(value -> {
            openable.setOpen(value);
            return DataResult.success(null);
        });
    }
}