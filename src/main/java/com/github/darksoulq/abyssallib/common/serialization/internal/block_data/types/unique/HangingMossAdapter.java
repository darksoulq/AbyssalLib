package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types.unique;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.HangingMoss;

public class HangingMossAdapter extends Adapter<HangingMoss> {
    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof HangingMoss;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, HangingMoss value) {
        return Codecs.BOOLEAN.encode(ops, value.isTip());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, BlockData base) {
        if (!(base instanceof HangingMoss hangingMoss))
            return DataResult.error(DataError.custom("Base is not HangingMoss, got: " + base.getClass().getSimpleName()));

        return Codecs.BOOLEAN.decode(ops, input).flatMap(value -> {
            hangingMoss.setTip(value);
            return DataResult.success(null);
        });
    }
}