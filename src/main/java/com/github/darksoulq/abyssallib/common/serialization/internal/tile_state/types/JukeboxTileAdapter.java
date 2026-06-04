package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.TileAdapter;
import org.bukkit.block.Jukebox;
import org.bukkit.block.TileState;

import java.util.Map;

public class JukeboxTileAdapter extends TileAdapter<Jukebox> {

    @Override
    public boolean doesApply(TileState state) {
        return state instanceof Jukebox;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, Jukebox value) {
        if (!value.getRecord().isEmpty()) {
            return Codecs.ITEM_STACK.encode(ops, value.getRecord());
        }
        return DataResult.success(ops.createMap(Map.of()));
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, TileState base) {
        if (!(base instanceof Jukebox jukebox)) return DataResult.success(null);

        return Codecs.ITEM_STACK.decode(ops, input).flatMap(item -> {
            try {
                jukebox.setRecord(item);
                return DataResult.success(null);
            } catch (Exception e) {
                return DataResult.error("Failed to set record: " + e.getMessage());
            }
        });
    }
}