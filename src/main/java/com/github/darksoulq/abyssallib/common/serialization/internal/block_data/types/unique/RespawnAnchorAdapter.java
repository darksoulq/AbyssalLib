package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types.unique;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.RespawnAnchor;

public class RespawnAnchorAdapter extends Adapter<RespawnAnchor> {
    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof RespawnAnchor;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, RespawnAnchor value) {
        return Codecs.INT.encode(ops, value.getCharges());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, BlockData base) {
        if (!(base instanceof RespawnAnchor respawnAnchor))
            return DataResult.error(DataError.custom("Base is not RespawnAnchor, got: " + base.getClass().getSimpleName()));

        return Codecs.INT.decode(ops, input).flatMap(value -> {
            if (value > respawnAnchor.getMaximumCharges()) {
                return DataResult.error(DataError.outOfBounds(value, 0, respawnAnchor.getMaximumCharges()));
            }
            respawnAnchor.setCharges(value);
            return DataResult.success(null);
        });
    }
}