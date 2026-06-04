package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.Axis;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Orientable;

public class OrientableAdapter extends Adapter<Orientable> {
    private static final Codec<Axis> CODEC = Codec.enumCodec(Axis.class);

    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof Orientable;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, Orientable value) {
        return CODEC.encode(ops, value.getAxis());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, BlockData base) {
        if (!(base instanceof Orientable orientable))
            return DataResult.error(DataError.custom("Base is not Orientable, got: " + base.getClass().getSimpleName()));
        return CODEC.decode(ops, input).flatMap(value -> {
            if (!orientable.getAxes().contains(value))
                return DataResult.error(DataError.custom("Invalid Axis '" + value.name() + "' for Orientable block, allowed axes: " + orientable.getAxes()));
            orientable.setAxis(value);
            return DataResult.success(null);
        });
    }
}