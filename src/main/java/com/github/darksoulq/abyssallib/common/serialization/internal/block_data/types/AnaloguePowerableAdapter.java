package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.AnaloguePowerable;
import org.bukkit.block.data.BlockData;

public class AnaloguePowerableAdapter extends Adapter<AnaloguePowerable> {
    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof AnaloguePowerable;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, AnaloguePowerable value) {
        return Codecs.INT.encode(ops, value.getPower());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, BlockData base) {
        if (!(base instanceof AnaloguePowerable powerable))
            return DataResult.error(DataError.custom("Base is not AnaloguePowerable, got: " + base.getClass().getSimpleName()));

        return Codecs.INT.decode(ops, input).flatMap(value -> {
            if (value > powerable.getMaximumPower()) {
                return DataResult.error(DataError.custom("Power value (" + value + ") exceeds maximum (" + powerable.getMaximumPower() + ")"));
            }
            powerable.setPower(value);
            return DataResult.success(null);
        });
    }
}