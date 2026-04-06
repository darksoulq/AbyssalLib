package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.TileAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.block.Crafter;
import org.bukkit.block.TileState;

public class CrafterTileAdapter extends TileAdapter<Crafter> {

    @Override
    public boolean doesApply(TileState state) {
        return state instanceof Crafter;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, Crafter value) throws Codec.CodecException {
        return Codecs.INT.encode(ops, value.getCraftingTicks());
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, TileState base) throws Codec.CodecException {
        if (!(base instanceof Crafter crafter)) return;
        Try.of(() -> Codecs.INT.decode(ops, input)).onSuccess(crafter::setCraftingTicks);
    }
}