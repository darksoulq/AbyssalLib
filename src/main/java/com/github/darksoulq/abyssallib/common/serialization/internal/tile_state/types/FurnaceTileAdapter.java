package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.TileAdapter;
import org.bukkit.block.Furnace;
import org.bukkit.block.TileState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FurnaceTileAdapter extends TileAdapter<Furnace> {

    @Override
    public boolean doesApply(TileState state) {
        return state instanceof Furnace;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, Furnace value) {
        Map<D, D> map = new HashMap<>();
        List<DataError> warnings = new ArrayList<>();

        DataResult<D> burnTimeRes = Codecs.INT.encode(ops, (int) value.getBurnTime()).prependPath("burn_time");
        if (burnTimeRes.isError()) {
            warnings.add(burnTimeRes.dataError().orElseGet(() -> DataError.custom(burnTimeRes.error().get())));
        } else {
            map.put(ops.createString("burn_time"), burnTimeRes.getOrThrow());
            if (burnTimeRes.isPartial()) warnings.addAll(burnTimeRes.warnings());
        }

        DataResult<D> cookTimeRes = Codecs.INT.encode(ops, (int) value.getCookTime()).prependPath("cook_time");
        if (cookTimeRes.isError()) {
            warnings.add(cookTimeRes.dataError().orElseGet(() -> DataError.custom(cookTimeRes.error().get())));
        } else {
            map.put(ops.createString("cook_time"), cookTimeRes.getOrThrow());
            if (cookTimeRes.isPartial()) warnings.addAll(cookTimeRes.warnings());
        }

        DataResult<D> cookTimeTotalRes = Codecs.INT.encode(ops, value.getCookTimeTotal()).prependPath("cook_time_total");
        if (cookTimeTotalRes.isError()) {
            warnings.add(cookTimeTotalRes.dataError().orElseGet(() -> DataError.custom(cookTimeTotalRes.error().get())));
        } else {
            map.put(ops.createString("cook_time_total"), cookTimeTotalRes.getOrThrow());
            if (cookTimeTotalRes.isPartial()) warnings.addAll(cookTimeTotalRes.warnings());
        }

        try {
            DataResult<D> multiplierRes = Codecs.DOUBLE.encode(ops, value.getCookSpeedMultiplier()).prependPath("cook_speed_multiplier");
            if (multiplierRes.isError()) {
                warnings.add(multiplierRes.dataError().orElseGet(() -> DataError.custom(multiplierRes.error().get())));
            } else {
                map.put(ops.createString("cook_speed_multiplier"), multiplierRes.getOrThrow());
                if (multiplierRes.isPartial()) warnings.addAll(multiplierRes.warnings());
            }
        } catch (Exception e) {
            warnings.add(DataError.custom("Failed to read cook_speed_multiplier: " + e.getMessage()));
        }

        return warnings.isEmpty() ? DataResult.success(ops.createMap(map)) : DataResult.partial(ops.createMap(map), warnings);
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, TileState base) {
        if (!(base instanceof Furnace furnace)) return DataResult.success(null);

        return ops.getMap(input)
            .map(DataResult::success)
            .orElseGet(() -> DataResult.error(DataError.typeMismatch("Map", "Unknown")))
            .flatMap(map -> {
                List<DataError> warnings = new ArrayList<>();

                D burnTime = map.get(ops.createString("burn_time"));
                if (burnTime != null) {
                    DataResult<Integer> res = Codecs.INT.decode(ops, burnTime).prependPath("burn_time");
                    if (res.isError()) {
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    } else {
                        furnace.setBurnTime(res.getOrThrow().shortValue());
                        if (res.isPartial()) warnings.addAll(res.warnings());
                    }
                }

                D cookTime = map.get(ops.createString("cook_time"));
                if (cookTime != null) {
                    DataResult<Integer> res = Codecs.INT.decode(ops, cookTime).prependPath("cook_time");
                    if (res.isError()) {
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    } else {
                        furnace.setCookTime(res.getOrThrow().shortValue());
                        if (res.isPartial()) warnings.addAll(res.warnings());
                    }
                }

                D cookTimeTotal = map.get(ops.createString("cook_time_total"));
                if (cookTimeTotal != null) {
                    DataResult<Integer> res = Codecs.INT.decode(ops, cookTimeTotal).prependPath("cook_time_total");
                    if (res.isError()) {
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    } else {
                        furnace.setCookTimeTotal(res.getOrThrow());
                        if (res.isPartial()) warnings.addAll(res.warnings());
                    }
                }

                D multiplier = map.get(ops.createString("cook_speed_multiplier"));
                if (multiplier != null) {
                    DataResult<Double> res = Codecs.DOUBLE.decode(ops, multiplier).prependPath("cook_speed_multiplier");
                    if (res.isError()) {
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    } else {
                        try {
                            furnace.setCookSpeedMultiplier(res.getOrThrow());
                        } catch (Exception e) {
                            warnings.add(DataError.custom("Failed to set cook_speed_multiplier: " + e.getMessage()));
                        }
                        if (res.isPartial()) warnings.addAll(res.warnings());
                    }
                }

                return warnings.isEmpty() ? DataResult.success(null) : DataResult.partial(null, warnings);
            });
    }
}