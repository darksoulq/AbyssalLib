package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types.unique;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Snow;

public class SnowAdapter extends Adapter<Snow> {
    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof Snow;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, Snow value) {
        return Codecs.INT.encode(ops, value.getLayers());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, BlockData base) {
        if (!(base instanceof Snow snow))
            return DataResult.error(DataError.custom("Base is not Snow, got: " + base.getClass().getSimpleName()));

        return Codecs.INT.decode(ops, input).flatMap(value -> {
            if (value < snow.getMinimumLayers() || value > snow.getMaximumLayers()) {
                return DataResult.error(DataError.outOfBounds(value, snow.getMinimumLayers(), snow.getMaximumLayers()));
            }
            snow.setLayers(value);
            return DataResult.success(null);
        });
    }
}