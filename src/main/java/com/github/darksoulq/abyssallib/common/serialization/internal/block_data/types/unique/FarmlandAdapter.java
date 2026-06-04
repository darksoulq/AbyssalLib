package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types.unique;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Farmland;

public class FarmlandAdapter extends Adapter<Farmland> {
    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof Farmland;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, Farmland value) {
        return Codecs.INT.encode(ops, value.getMoisture());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, BlockData base) {
        if (!(base instanceof Farmland farmland))
            return DataResult.error(DataError.custom("Base is not Farmland, got: " + base.getClass().getSimpleName()));

        return Codecs.INT.decode(ops, input).flatMap(value -> {
            if (value > farmland.getMaximumMoisture()) {
                return DataResult.error(DataError.custom("Moisture value (" + value + ") exceeds maximum (" + farmland.getMaximumMoisture() + ")"));
            }
            farmland.setMoisture(value);
            return DataResult.success(null);
        });
    }
}