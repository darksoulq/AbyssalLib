package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
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
    public <D> D serialize(DynamicOps<D> ops, Rotatable value) throws Codec.CodecException {
        return CODEC.encode(ops, value.getRotation());
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, BlockData base) throws Codec.CodecException {
        if (!(base instanceof Rotatable rotatable)) return;
        BlockFace value = CODEC.decode(ops, input);
        rotatable.setRotation(value);
    }
}
