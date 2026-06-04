package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;

public class BisectedAdapter extends Adapter<Bisected> {
    private static final Codec<Bisected.Half> CODEC = Codec.enumCodec(Bisected.Half.class);

    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof Bisected;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, Bisected value) {
        return CODEC.encode(ops, value.getHalf());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, BlockData base) {
        if (!(base instanceof Bisected bisected))
            return DataResult.error(DataError.custom("Base is not Bisected, got: " + base.getClass().getSimpleName()));

        return CODEC.decode(ops, input).flatMap(value -> {
            bisected.setHalf(value);
            return DataResult.success(null);
        });
    }
}