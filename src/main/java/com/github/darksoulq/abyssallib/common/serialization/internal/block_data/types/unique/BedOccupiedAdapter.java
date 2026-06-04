package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types.unique;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Bed;

public class BedOccupiedAdapter extends Adapter<Bed> {
    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof Bed;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, Bed value) {
        return Codecs.BOOLEAN.encode(ops, value.isOccupied());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, BlockData base) {
        if (!(base instanceof Bed bed))
            return DataResult.error(DataError.custom("Base is not Bed, got: " + base.getClass().getSimpleName()));

        return Codecs.BOOLEAN.decode(ops, input).flatMap(occupied -> {
            bed.setOccupied(occupied);
            return DataResult.success(null);
        });
    }
}