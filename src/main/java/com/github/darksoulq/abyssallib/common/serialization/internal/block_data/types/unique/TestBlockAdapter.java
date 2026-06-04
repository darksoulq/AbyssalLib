package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types.unique;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.TestBlock;

public class TestBlockAdapter extends Adapter<TestBlock> {
    private static final Codec<TestBlock.Mode> CODEC = Codec.enumCodec(TestBlock.Mode.class);

    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof TestBlock;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, TestBlock value) {
        return CODEC.encode(ops, value.getMode());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, BlockData base) {
        if (!(base instanceof TestBlock testBlock))
            return DataResult.error(DataError.custom("Base is not TestBlock, got: " + base.getClass().getSimpleName()));

        return CODEC.decode(ops, input).flatMap(value -> {
            testBlock.setMode(value);
            return DataResult.success(null);
        });
    }
}