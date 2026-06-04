package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types.unique;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.SculkCatalyst;

public class SculkCatalystAdapter extends Adapter<SculkCatalyst> {
    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof SculkCatalyst;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, SculkCatalyst value) {
        return Codecs.BOOLEAN.encode(ops, value.isBloom());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, BlockData base) {
        if (!(base instanceof SculkCatalyst sculkCatalyst))
            return DataResult.error(DataError.custom("Base is not SculkCatalyst, got: " + base.getClass().getSimpleName()));

        return Codecs.BOOLEAN.decode(ops, input).flatMap(value -> {
            sculkCatalyst.setBloom(value);
            return DataResult.success(null);
        });
    }
}