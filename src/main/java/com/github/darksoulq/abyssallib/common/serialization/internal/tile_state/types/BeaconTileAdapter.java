package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.TileAdapter;
import org.bukkit.block.Beacon;
import org.bukkit.block.TileState;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeaconTileAdapter extends TileAdapter<Beacon> {

    @Override
    public boolean doesApply(TileState state) {
        return state instanceof Beacon;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, Beacon value) {
        Map<D, D> map = new HashMap<>();
        List<DataError> warnings = new ArrayList<>();

        if (value.getPrimaryEffect() != null) {
            DataResult<D> res = ExtraCodecs.POTION_EFFECT_TYPE.encode(ops, value.getPrimaryEffect().getType()).prependPath("primary");
            if (res.isError()) {
                warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
            } else {
                map.put(ops.createString("primary"), res.getOrThrow());
                if (res.isPartial()) warnings.addAll(res.warnings());
            }
        }

        if (value.getSecondaryEffect() != null) {
            DataResult<D> res = ExtraCodecs.POTION_EFFECT_TYPE.encode(ops, value.getSecondaryEffect().getType()).prependPath("secondary");
            if (res.isError()) {
                warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
            } else {
                map.put(ops.createString("secondary"), res.getOrThrow());
                if (res.isPartial()) warnings.addAll(res.warnings());
            }
        }

        return warnings.isEmpty() ? DataResult.success(ops.createMap(map)) : DataResult.partial(ops.createMap(map), warnings);
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, TileState base) {
        if (!(base instanceof Beacon beacon)) return DataResult.success(null);

        return ops.getMap(input)
            .map(DataResult::success)
            .orElseGet(() -> DataResult.error(DataError.typeMismatch("Map", "Unknown")))
            .flatMap(map -> {
                List<DataError> warnings = new ArrayList<>();

                D primaryData = map.get(ops.createString("primary"));
                if (primaryData != null) {
                    DataResult<PotionEffectType> res = ExtraCodecs.POTION_EFFECT_TYPE.decode(ops, primaryData).prependPath("primary");
                    if (res.isError()) {
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    } else {
                        beacon.setPrimaryEffect(res.getOrThrow());
                        if (res.isPartial()) warnings.addAll(res.warnings());
                    }
                }

                D secondaryData = map.get(ops.createString("secondary"));
                if (secondaryData != null) {
                    DataResult<PotionEffectType> res = ExtraCodecs.POTION_EFFECT_TYPE.decode(ops, secondaryData).prependPath("secondary");
                    if (res.isError()) {
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    } else {
                        beacon.setSecondaryEffect(res.getOrThrow());
                        if (res.isPartial()) warnings.addAll(res.warnings());
                    }
                }

                return warnings.isEmpty() ? DataResult.success(null) : DataResult.partial(null, warnings);
            });
    }
}