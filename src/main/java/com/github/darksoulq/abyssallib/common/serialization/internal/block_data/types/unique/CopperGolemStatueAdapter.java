package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types.unique;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
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
    public <D> DataResult<D> serialize(DynamicOps<D> ops, CopperGolemStatue value) {
        return CODEC.encode(ops, value.getCopperGolemPose());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, BlockData base) {
        if (!(base instanceof CopperGolemStatue copperGolemStatue))
            return DataResult.error(DataError.custom("Base is not CopperGolemStatue, got: " + base.getClass().getSimpleName()));

        return CODEC.decode(ops, input).flatMap(value -> {
            copperGolemStatue.setCopperGolemPose(value);
            return DataResult.success(null);
        });
    }
}