package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types;

import com.github.darksoulq.abyssallib.common.serialization.*;
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
    public <D> DataResult<D> serialize(DynamicOps<D> ops, MultipleFacing value) {
        Map<BlockFace, Boolean> result = new HashMap<>();
        for (BlockFace face : value.getAllowedFaces()) {
            result.put(face, value.hasFace(face));
        }
        return CODEC.encode(ops, result);
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, BlockData base) {
        if (!(base instanceof MultipleFacing facing))
            return DataResult.error(DataError.custom("Base is not MultipleFacing, got: " + base.getClass().getSimpleName()));
        return CODEC.decode(ops, input).flatMap(value -> {
            Set<BlockFace> allowed = facing.getAllowedFaces();
            for (Map.Entry<BlockFace, Boolean> entry : value.entrySet()) {
                BlockFace face = entry.getKey();
                if (!allowed.contains(face))
                    return DataResult.error(DataError.custom("Invalid BlockFace '" + face.name() + "' for MultipleFacing block, allowed faces: " + allowed));
                facing.setFace(face, entry.getValue());
            }
            return DataResult.success(null);
        });
    }
}