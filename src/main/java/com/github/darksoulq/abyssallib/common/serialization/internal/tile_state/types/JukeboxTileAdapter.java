package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.TileAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.block.Jukebox;
import org.bukkit.block.TileState;

import java.util.Map;

public class JukeboxTileAdapter extends TileAdapter<Jukebox> {

    @Override
    public boolean doesApply(TileState state) {
        return state instanceof Jukebox;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, Jukebox value) throws Codec.CodecException {
        if (!value.getRecord().isEmpty()) {
            return Codecs.ITEM_STACK.encode(ops, value.getRecord());
        }
        return ops.createMap(Map.of());
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, TileState base) throws Codec.CodecException {
        if (!(base instanceof Jukebox jukebox)) return;
        Try.of(() -> Codecs.ITEM_STACK.decode(ops, input)).onSuccess(jukebox::setRecord);
    }
}