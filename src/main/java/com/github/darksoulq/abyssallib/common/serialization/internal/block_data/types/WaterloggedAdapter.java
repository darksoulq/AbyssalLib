package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;

public class WaterloggedAdapter extends Adapter<Waterlogged> {
    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof Waterlogged;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, Waterlogged value) {
        return Codecs.BOOLEAN.encode(ops, value.isWaterlogged());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, BlockData base) {
        if (!(base instanceof Waterlogged waterlogged))
            return DataResult.error(DataError.custom("Base is not Waterlogged, got: " + base.getClass().getSimpleName()));
        return Codecs.BOOLEAN.decode(ops, input).flatMap(value -> {
            waterlogged.setWaterlogged(value);
            return DataResult.success(null);
        });
    }
}