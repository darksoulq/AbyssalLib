package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
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
    public <D> D serialize(DynamicOps<D> ops, FaceAttachable value) throws Codec.CodecException {
        return CODEC.encode(ops, value.getAttachedFace());
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, BlockData base) throws Codec.CodecException {
        if (!(base instanceof FaceAttachable faceAttachable)) return;
        FaceAttachable.AttachedFace value = CODEC.decode(ops, input);
        faceAttachable.setAttachedFace(value);
    }
}
