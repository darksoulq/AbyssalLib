package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.TileAdapter;
import org.bukkit.block.SculkSensor;
import org.bukkit.block.TileState;

public class SculkSensorTileAdapter extends TileAdapter<SculkSensor> {

    @Override
    public boolean doesApply(TileState state) {
        return state instanceof SculkSensor;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, SculkSensor value) {
        return Codecs.INT.encode(ops, value.getLastVibrationFrequency());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, TileState base) {
        if (!(base instanceof SculkSensor sensor)) return DataResult.success(null);

        return Codecs.INT.decode(ops, input).flatMap(freq -> {
            sensor.setLastVibrationFrequency(freq);
            return DataResult.success(null);
        });
    }
}