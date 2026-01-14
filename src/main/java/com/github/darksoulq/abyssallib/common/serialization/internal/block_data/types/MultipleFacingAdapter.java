package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.MultipleFacing;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MultipleFacingAdapter extends Adapter<MultipleFacing> {
    private static final Codec<Map<BlockFace, Boolean>> CODEC = Codec.map(Codec.enumCodec(BlockFace.class), Codecs.BOOLEAN);

    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof MultipleFacing;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, MultipleFacing value) throws Codec.CodecException {
        Map<BlockFace, Boolean> result = new HashMap<>();
        for (BlockFace face : value.getAllowedFaces()) {
            result.put(face, value.hasFace(face));
        }
        return CODEC.encode(ops, result);
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, BlockData base) throws Codec.CodecException {
        if (!(base instanceof MultipleFacing facing)) return;
        Map<BlockFace, Boolean> value = CODEC.decode(ops, input);
        Set<BlockFace> allowed = facing.getAllowedFaces();

        value.forEach((face, state) -> {
            if (!allowed.contains(face)) return;
            facing.setFace(face, state);
        });
    }
}
