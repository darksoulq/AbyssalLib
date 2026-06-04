package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
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
    public <D> DataResult<D> serialize(DynamicOps<D> ops, Directional value) {
        return CODEC.encode(ops, value.getFacing());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, BlockData base) {
        if (!(base instanceof Directional directional))
            return DataResult.error(DataError.custom("Base is not Directional, got: " + base.getClass().getSimpleName()));

        return CODEC.decode(ops, input).flatMap(face -> {
            if (!directional.getFaces().contains(face)) {
                return DataResult.error(DataError.custom("Invalid BlockFace '" + face.name() + "' for Directional block, allowed faces: " + directional.getFaces()));
            }
            directional.setFacing(face);
            return DataResult.success(null);
        });
    }
}