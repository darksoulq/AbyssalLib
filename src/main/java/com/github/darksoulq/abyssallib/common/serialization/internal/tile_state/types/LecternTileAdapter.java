package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.TileAdapter;
import org.bukkit.block.Lectern;
import org.bukkit.block.TileState;

public class LecternTileAdapter extends TileAdapter<Lectern> {

    @Override
    public boolean doesApply(TileState state) {
        return state instanceof Lectern;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, Lectern value) {
        return Codecs.INT.encode(ops, value.getPage());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, TileState base) {
        if (!(base instanceof Lectern lectern)) return DataResult.success(null);

        return Codecs.INT.decode(ops, input).flatMap(page -> {
            lectern.setPage(page);
            return DataResult.success(null);
        });
    }
}