package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.TileAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.block.SculkSensor;
import org.bukkit.block.TileState;

public class SculkSensorTileAdapter extends TileAdapter<SculkSensor> {

    @Override
    public boolean doesApply(TileState state) {
        return state instanceof SculkSensor;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, SculkSensor value) throws Codec.CodecException {
        return Codecs.INT.encode(ops, value.getLastVibrationFrequency());
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, TileState base) throws Codec.CodecException {
        if (!(base instanceof SculkSensor sensor)) return;
        Try.of(() -> Codecs.INT.decode(ops, input)).onSuccess(sensor::setLastVibrationFrequency);
    }
}