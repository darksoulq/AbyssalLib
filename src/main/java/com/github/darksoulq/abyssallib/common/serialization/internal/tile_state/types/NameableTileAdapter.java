package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.TileAdapter;
import org.bukkit.Nameable;
import org.bukkit.block.TileState;

public class NameableTileAdapter extends TileAdapter<Nameable> {

    @Override
    public boolean doesApply(TileState state) {
        return state instanceof Nameable;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, Nameable value) {
        if (value.customName() == null) {
            return DataResult.error(DataError.custom("No custom name"));
        }
        return Codecs.TEXT_COMPONENT.encode(ops, value.customName());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, TileState base) {
        if (!(base instanceof Nameable nameable)) return DataResult.success(null);

        return Codecs.TEXT_COMPONENT.decode(ops, input).flatMap(component -> {
            nameable.customName(component);
            return DataResult.success(null);
        });
    }
}