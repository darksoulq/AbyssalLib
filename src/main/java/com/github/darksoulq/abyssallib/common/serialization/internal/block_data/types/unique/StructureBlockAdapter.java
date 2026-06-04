package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types.unique;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.StructureBlock;

public class StructureBlockAdapter extends Adapter<StructureBlock> {
    private static final Codec<StructureBlock.Mode> CODEC = Codec.enumCodec(StructureBlock.Mode.class);

    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof StructureBlock;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, StructureBlock value) {
        return CODEC.encode(ops, value.getMode());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, BlockData base) {
        if (!(base instanceof StructureBlock structureBlock))
            return DataResult.error(DataError.custom("Base is not StructureBlock, got: " + base.getClass().getSimpleName()));

        return CODEC.decode(ops, input).flatMap(value -> {
            structureBlock.setMode(value);
            return DataResult.success(null);
        });
    }
}