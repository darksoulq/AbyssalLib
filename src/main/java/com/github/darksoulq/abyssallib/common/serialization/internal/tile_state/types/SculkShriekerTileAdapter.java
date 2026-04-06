package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.TileAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.block.SculkShrieker;
import org.bukkit.block.TileState;

public class SculkShriekerTileAdapter extends TileAdapter<SculkShrieker> {

    @Override
    public boolean doesApply(TileState state) {
        return state instanceof SculkShrieker;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, SculkShrieker value) throws Codec.CodecException {
        return Codecs.INT.encode(ops, value.getWarningLevel());
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, TileState base) throws Codec.CodecException {
        if (!(base instanceof SculkShrieker shrieker)) return;
        Try.of(() -> Codecs.INT.decode(ops, input)).onSuccess(shrieker::setWarningLevel);
    }
}