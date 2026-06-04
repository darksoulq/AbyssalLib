package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types.unique;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.Orientation;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Crafter;

public class CrafterOrientationAdapter extends Adapter<Crafter> {
    private static final Codec<Orientation> CODEC = Codec.enumCodec(Orientation.class);

    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof Crafter;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, Crafter value) {
        return CODEC.encode(ops, value.getOrientation());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, BlockData base) {
        if (!(base instanceof Crafter crafter))
            return DataResult.error(DataError.custom("Base is not Crafter, got: " + base.getClass().getSimpleName()));

        return CODEC.decode(ops, input).flatMap(value -> {
            crafter.setOrientation(value);
            return DataResult.success(null);
        });
    }
}