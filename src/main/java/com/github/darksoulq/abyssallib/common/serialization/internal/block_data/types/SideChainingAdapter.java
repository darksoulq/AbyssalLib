package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.SideChaining;

public class SideChainingAdapter extends Adapter<SideChaining> {
    private static final Codec<SideChaining.ChainPart> CODEC = Codec.enumCodec(SideChaining.ChainPart.class);

    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof SideChaining;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, SideChaining value) {
        return CODEC.encode(ops, value.getSideChain());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, BlockData base) {
        if (!(base instanceof SideChaining sideChaining))
            return DataResult.error(DataError.custom("Base is not SideChaining, got: " + base.getClass().getSimpleName()));
        return CODEC.decode(ops, input).flatMap(value -> {
            sideChaining.setSideChain(value);
            return DataResult.success(null);
        });
    }
}