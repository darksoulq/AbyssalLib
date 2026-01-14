package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types.unique;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.CopperGolemStatue;

public class CopperGolemStatueAdapter extends Adapter<CopperGolemStatue> {
    private static final Codec<CopperGolemStatue.Pose> CODEC = Codec.enumCodec(CopperGolemStatue.Pose.class);

    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof CopperGolemStatue;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, CopperGolemStatue value) throws Codec.CodecException {
        return CODEC.encode(ops, value.getCopperGolemPose());
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, BlockData base) throws Codec.CodecException {
        if (!(base instanceof CopperGolemStatue copperGolemStatue)) return;
        CopperGolemStatue.Pose value = CODEC.decode(ops, input);
        copperGolemStatue.setCopperGolemPose(value);
    }
}
