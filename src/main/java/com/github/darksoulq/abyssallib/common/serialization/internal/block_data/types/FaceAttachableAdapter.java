package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.FaceAttachable;

public class FaceAttachableAdapter extends Adapter<FaceAttachable> {
    private static final Codec<FaceAttachable.AttachedFace> CODEC = Codec.enumCodec(FaceAttachable.AttachedFace.class);

    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof FaceAttachable;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, FaceAttachable value) {
        return CODEC.encode(ops, value.getAttachedFace());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, BlockData base) {
        if (!(base instanceof FaceAttachable faceAttachable))
            return DataResult.error(DataError.custom("Base is not FaceAttachable, got: " + base.getClass().getSimpleName()));

        return CODEC.decode(ops, input).flatMap(value -> {
            faceAttachable.setAttachedFace(value);
            return DataResult.success(null);
        });
    }
}