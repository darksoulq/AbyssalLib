package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types.unique;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
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
    public <D> D serialize(DynamicOps<D> ops, TestBlock value) throws Codec.CodecException {
        return CODEC.encode(ops, value.getMode());
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, BlockData base) throws Codec.CodecException {
        if (!(base instanceof TestBlock testBlock)) return;
        TestBlock.Mode value = CODEC.decode(ops, input);
        testBlock.setMode(value);
    }
}
