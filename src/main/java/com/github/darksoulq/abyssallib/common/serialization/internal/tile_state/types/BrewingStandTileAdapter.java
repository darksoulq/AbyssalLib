package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.TileAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.TileState;

import java.util.HashMap;
import java.util.Map;

public class BrewingStandTileAdapter extends TileAdapter<BrewingStand> {

    @Override
    public boolean doesApply(TileState state) {
        return state instanceof BrewingStand;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, BrewingStand value) throws Codec.CodecException {
        Map<D, D> map = new HashMap<>();

        map.put(ops.createString("brewing_time"), Codecs.INT.encode(ops, value.getBrewingTime()));
        map.put(ops.createString("fuel_level"), Codecs.INT.encode(ops, value.getFuelLevel()));

        Try.run(() -> map.put(ops.createString("recipe_brew_time"), Codecs.INT.encode(ops, value.getRecipeBrewTime())));

        return ops.createMap(map);
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, TileState base) throws Codec.CodecException {
        if (!(base instanceof BrewingStand stand)) return;
        Map<D, D> map = ops.getMap(input).orElseThrow(() -> new Codec.CodecException("Expected map for BrewingStand"));

        D brewTime = map.get(ops.createString("brewing_time"));
        if (brewTime != null) {
            stand.setBrewingTime(Codecs.INT.decode(ops, brewTime));
        }

        D fuelLevel = map.get(ops.createString("fuel_level"));
        if (fuelLevel != null) {
            stand.setFuelLevel(Codecs.INT.decode(ops, fuelLevel));
        }

        D recipeTime = map.get(ops.createString("recipe_brew_time"));
        if (recipeTime != null) {
            Try.run(() -> stand.setRecipeBrewTime(Codecs.INT.decode(ops, recipeTime)));
        }
    }
}