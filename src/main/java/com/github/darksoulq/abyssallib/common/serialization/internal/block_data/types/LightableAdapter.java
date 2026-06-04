package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Lightable;

public class LightableAdapter extends Adapter<Lightable> {
    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof Lightable;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, Lightable value) {
        return Codecs.BOOLEAN.encode(ops, value.isLit());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, BlockData base) {
        if (!(base instanceof Lightable lightable))
            return DataResult.error(DataError.custom("Base is not Lightable, got: " + base.getClass().getSimpleName()));
        return Codecs.BOOLEAN.decode(ops, input).flatMap(value -> {
            lightable.setLit(value);
            return DataResult.success(null);
        });
    }
}