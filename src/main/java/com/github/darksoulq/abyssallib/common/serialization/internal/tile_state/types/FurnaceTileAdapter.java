package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.TileAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.block.Furnace;
import org.bukkit.block.TileState;

import java.util.HashMap;
import java.util.Map;

public class FurnaceTileAdapter extends TileAdapter<Furnace> {

    @Override
    public boolean doesApply(TileState state) {
        return state instanceof Furnace;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, Furnace value) throws Codec.CodecException {
        Map<D, D> map = new HashMap<>();

        map.put(ops.createString("burn_time"), Codecs.INT.encode(ops, (int) value.getBurnTime()));
        map.put(ops.createString("cook_time"), Codecs.INT.encode(ops, (int) value.getCookTime()));
        map.put(ops.createString("cook_time_total"), Codecs.INT.encode(ops, value.getCookTimeTotal()));

        Try.run(() -> map.put(ops.createString("cook_speed_multiplier"), Codecs.DOUBLE.encode(ops, value.getCookSpeedMultiplier())));

        return ops.createMap(map);
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, TileState base) throws Codec.CodecException {
        if (!(base instanceof Furnace furnace)) return;
        Map<D, D> map = ops.getMap(input).orElseThrow(() -> new Codec.CodecException("Expected map for Furnace"));

        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("burn_time")))).onSuccess(v -> furnace.setBurnTime(v.shortValue()));
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("cook_time")))).onSuccess(v -> furnace.setCookTime(v.shortValue()));
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("cook_time_total")))).onSuccess(furnace::setCookTimeTotal);

        D multiplier = map.get(ops.createString("cook_speed_multiplier"));
        if (multiplier != null) {
            Try.of(() -> Codecs.DOUBLE.decode(ops, multiplier)).onSuccess(furnace::setCookSpeedMultiplier);
        }
    }
}