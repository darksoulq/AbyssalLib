package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.TileAdapter;
import org.bukkit.block.SculkShrieker;
import org.bukkit.block.TileState;

public class SculkShriekerTileAdapter extends TileAdapter<SculkShrieker> {

    @Override
    public boolean doesApply(TileState state) {
        return state instanceof SculkShrieker;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, SculkShrieker value) {
        return Codecs.INT.encode(ops, value.getWarningLevel());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, TileState base) {
        if (!(base instanceof SculkShrieker shrieker)) return DataResult.success(null);

        return Codecs.INT.decode(ops, input).flatMap(level -> {
            shrieker.setWarningLevel(level);
            return DataResult.success(null);
        });
    }
}