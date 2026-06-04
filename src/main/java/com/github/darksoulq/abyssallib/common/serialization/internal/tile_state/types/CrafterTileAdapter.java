package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.TileAdapter;
import org.bukkit.block.Crafter;
import org.bukkit.block.TileState;

public class CrafterTileAdapter extends TileAdapter<Crafter> {

    @Override
    public boolean doesApply(TileState state) {
        return state instanceof Crafter;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, Crafter value) {
        return Codecs.INT.encode(ops, value.getCraftingTicks());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, TileState base) {
        if (!(base instanceof Crafter crafter)) return DataResult.success(null);

        return Codecs.INT.decode(ops, input).flatMap(ticks -> {
            crafter.setCraftingTicks(ticks);
            return DataResult.success(null);
        });
    }
}