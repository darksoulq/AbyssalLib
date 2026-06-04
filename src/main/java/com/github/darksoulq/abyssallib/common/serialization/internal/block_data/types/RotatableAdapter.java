package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Rotatable;

public class RotatableAdapter extends Adapter<Rotatable> {
    private static final Codec<BlockFace> CODEC = Codec.enumCodec(BlockFace.class);

    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof Rotatable;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, Rotatable value) {
        return CODEC.encode(ops, value.getRotation());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, BlockData base) {
        if (!(base instanceof Rotatable rotatable))
            return DataResult.error(DataError.custom("Base is not Rotatable, got: " + base.getClass().getSimpleName()));
        return CODEC.decode(ops, input).flatMap(value -> {
            rotatable.setRotation(value);
            return DataResult.success(null);
        });
    }
}