package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
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
    public <D> D serialize(DynamicOps<D> ops, Lectern value) throws Codec.CodecException {
        return Codecs.INT.encode(ops, value.getPage());
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, TileState base) throws Codec.CodecException {
        if (!(base instanceof Lectern lectern)) return;
        lectern.setPage(Codecs.INT.decode(ops, input));
    }
}