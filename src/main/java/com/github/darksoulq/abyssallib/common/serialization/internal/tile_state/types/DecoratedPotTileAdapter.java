package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.TileAdapter;
import org.bukkit.Material;
import org.bukkit.block.DecoratedPot;
import org.bukkit.block.TileState;

import java.util.HashMap;
import java.util.Map;

public class DecoratedPotTileAdapter extends TileAdapter<DecoratedPot> {

    @Override
    public boolean doesApply(TileState state) {
        return state instanceof DecoratedPot;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, DecoratedPot value) {
        Map<D, D> map = new HashMap<>();

        map.put(ops.createString("front"), ops.createString(value.getSherd(DecoratedPot.Side.FRONT).name()));
        map.put(ops.createString("back"), ops.createString(value.getSherd(DecoratedPot.Side.BACK).name()));
        map.put(ops.createString("left"), ops.createString(value.getSherd(DecoratedPot.Side.LEFT).name()));
        map.put(ops.createString("right"), ops.createString(value.getSherd(DecoratedPot.Side.RIGHT).name()));

        return DataResult.success(ops.createMap(map));
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, TileState base) {
        if (!(base instanceof DecoratedPot pot)) return DataResult.success(null);

        return ops.getMap(input)
            .map(DataResult::success)
            .orElseGet(() -> DataResult.error(DataError.typeMismatch("Map", "Unknown")))
            .flatMap(map -> {
                ops.getStringValue(map.get(ops.createString("front"))).ifPresent(s -> {
                    try {
                        pot.setSherd(DecoratedPot.Side.FRONT, Material.valueOf(s));
                    } catch (Exception ignored) {
                    }
                });
                ops.getStringValue(map.get(ops.createString("back"))).ifPresent(s -> {
                    try {
                        pot.setSherd(DecoratedPot.Side.BACK, Material.valueOf(s));
                    } catch (Exception ignored) {
                    }
                });
                ops.getStringValue(map.get(ops.createString("left"))).ifPresent(s -> {
                    try {
                        pot.setSherd(DecoratedPot.Side.LEFT, Material.valueOf(s));
                    } catch (Exception ignored) {
                    }
                });
                ops.getStringValue(map.get(ops.createString("right"))).ifPresent(s -> {
                    try {
                        pot.setSherd(DecoratedPot.Side.RIGHT, Material.valueOf(s));
                    } catch (Exception ignored) {
                    }
                });

                return DataResult.success(null);
            });
    }
}