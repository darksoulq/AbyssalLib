package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;

public class DirectionalAdapter extends Adapter<Directional> {
    private static final Codec<BlockFace> CODEC = Codec.enumCodec(BlockFace.class);
    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof Directional;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, Directional value) throws Codec.CodecException {
        return CODEC.encode(ops, value.getFacing());
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, BlockData base) throws Codec.CodecException {
        if (!(base instanceof Directional directional)) return;
        BlockFace face = CODEC.decode(ops, input);
        if (!directional.getFaces().contains(face)) return;
        directional.setFacing(face);
    }
}
