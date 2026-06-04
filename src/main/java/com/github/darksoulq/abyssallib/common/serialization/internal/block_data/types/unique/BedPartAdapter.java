package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types.unique;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Bed;

public class BedPartAdapter extends Adapter<Bed> {
    private static final Codec<Bed.Part> CODEC = Codec.enumCodec(Bed.Part.class);

    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof Bed;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, Bed value) {
        return CODEC.encode(ops, value.getPart());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, BlockData base) {
        if (!(base instanceof Bed bed))
            return DataResult.error(DataError.custom("Base is not Bed, got: " + base.getClass().getSimpleName()));

        return CODEC.decode(ops, input).flatMap(part -> {
            bed.setPart(part);
            return DataResult.success(null);
        });
    }
}