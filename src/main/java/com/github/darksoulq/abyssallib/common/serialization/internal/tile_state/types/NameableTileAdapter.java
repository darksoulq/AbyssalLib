package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.TileAdapter;
import org.bukkit.Nameable;
import org.bukkit.block.TileState;


public class NameableTileAdapter extends TileAdapter<Nameable> {

    @Override
    public boolean doesApply(TileState state) {
        return state instanceof Nameable nameable && nameable.customName() != null;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, Nameable value) throws Codec.CodecException {
        return Codecs.TEXT_COMPONENT.encode(ops, value.customName());
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, TileState base) throws Codec.CodecException {
        if (!(base instanceof Nameable nameable)) return;
        nameable.customName(Codecs.TEXT_COMPONENT.decode(ops, input));
    }
}