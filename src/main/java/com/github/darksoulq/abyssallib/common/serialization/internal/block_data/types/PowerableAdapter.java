package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Powerable;

public class PowerableAdapter extends Adapter<Powerable> {
    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof Powerable;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, Powerable value) {
        return Codecs.BOOLEAN.encode(ops, value.isPowered());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, BlockData base) {
        if (!(base instanceof Powerable powerable))
            return DataResult.error(DataError.custom("Base is not Powerable, got: " + base.getClass().getSimpleName()));
        return Codecs.BOOLEAN.decode(ops, input).flatMap(value -> {
            powerable.setPowered(value);
            return DataResult.success(null);
        });
    }
}