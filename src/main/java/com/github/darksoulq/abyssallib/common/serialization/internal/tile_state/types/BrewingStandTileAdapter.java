package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.TileAdapter;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.TileState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BrewingStandTileAdapter extends TileAdapter<BrewingStand> {

    @Override
    public boolean doesApply(TileState state) {
        return state instanceof BrewingStand;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, BrewingStand value) {
        Map<D, D> map = new HashMap<>();
        List<DataError> warnings = new ArrayList<>();

        DataResult<D> brewTimeRes = Codecs.INT.encode(ops, value.getBrewingTime()).prependPath("brewing_time");
        if (brewTimeRes.isError()) {
            warnings.add(brewTimeRes.dataError().orElseGet(() -> DataError.custom(brewTimeRes.error().get())));
        } else {
            map.put(ops.createString("brewing_time"), brewTimeRes.getOrThrow());
            if (brewTimeRes.isPartial()) warnings.addAll(brewTimeRes.warnings());
        }

        DataResult<D> fuelLevelRes = Codecs.INT.encode(ops, value.getFuelLevel()).prependPath("fuel_level");
        if (fuelLevelRes.isError()) {
            warnings.add(fuelLevelRes.dataError().orElseGet(() -> DataError.custom(fuelLevelRes.error().get())));
        } else {
            map.put(ops.createString("fuel_level"), fuelLevelRes.getOrThrow());
            if (fuelLevelRes.isPartial()) warnings.addAll(fuelLevelRes.warnings());
        }

        try {
            DataResult<D> recipeTimeRes = Codecs.INT.encode(ops, value.getRecipeBrewTime()).prependPath("recipe_brew_time");
            if (recipeTimeRes.isError()) {
                warnings.add(recipeTimeRes.dataError().orElseGet(() -> DataError.custom(recipeTimeRes.error().get())));
            } else {
                map.put(ops.createString("recipe_brew_time"), recipeTimeRes.getOrThrow());
                if (recipeTimeRes.isPartial()) warnings.addAll(recipeTimeRes.warnings());
            }
        } catch (Exception e) {
            warnings.add(DataError.custom("Failed to read recipe_brew_time: " + e.getMessage()));
        }

        return warnings.isEmpty() ? DataResult.success(ops.createMap(map)) : DataResult.partial(ops.createMap(map), warnings);
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, TileState base) {
        if (!(base instanceof BrewingStand stand)) return DataResult.success(null);

        return ops.getMap(input)
            .map(DataResult::success)
            .orElseGet(() -> DataResult.error(DataError.typeMismatch("Map", "Unknown")))
            .flatMap(map -> {
                List<DataError> warnings = new ArrayList<>();

                D brewTime = map.get(ops.createString("brewing_time"));
                if (brewTime != null) {
                    DataResult<Integer> res = Codecs.INT.decode(ops, brewTime).prependPath("brewing_time");
                    if (res.isError()) {
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    } else {
                        stand.setBrewingTime(res.getOrThrow());
                        if (res.isPartial()) warnings.addAll(res.warnings());
                    }
                }

                D fuelLevel = map.get(ops.createString("fuel_level"));
                if (fuelLevel != null) {
                    DataResult<Integer> res = Codecs.INT.decode(ops, fuelLevel).prependPath("fuel_level");
                    if (res.isError()) {
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    } else {
                        stand.setFuelLevel(res.getOrThrow());
                        if (res.isPartial()) warnings.addAll(res.warnings());
                    }
                }

                D recipeTime = map.get(ops.createString("recipe_brew_time"));
                if (recipeTime != null) {
                    DataResult<Integer> res = Codecs.INT.decode(ops, recipeTime).prependPath("recipe_brew_time");
                    if (res.isError()) {
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    } else {
                        try {
                            stand.setRecipeBrewTime(res.getOrThrow());
                        } catch (Exception e) {
                            warnings.add(DataError.custom("Failed to apply recipe_brew_time: " + e.getMessage()));
                        }
                        if (res.isPartial()) warnings.addAll(res.warnings());
                    }
                }

                return warnings.isEmpty() ? DataResult.success(null) : DataResult.partial(null, warnings);
            });
    }
}