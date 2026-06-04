package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.TileAdapter;
import org.bukkit.block.EndGateway;
import org.bukkit.block.TileState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EndGatewayTileAdapter extends TileAdapter<EndGateway> {

    @Override
    public boolean doesApply(TileState state) {
        return state instanceof EndGateway;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, EndGateway value) {
        Map<D, D> map = new HashMap<>();
        List<DataError> warnings = new ArrayList<>();

        if (value.getExitLocation() != null) {
            DataResult<D> res = Codecs.LOCATION.encode(ops, value.getExitLocation()).prependPath("exit_location");
            if (res.isError()) {
                warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
            } else {
                map.put(ops.createString("exit_location"), res.getOrThrow());
                if (res.isPartial()) warnings.addAll(res.warnings());
            }
        }

        DataResult<D> exactTeleportRes = Codecs.BOOLEAN.encode(ops, value.isExactTeleport()).prependPath("exact_teleport");
        if (exactTeleportRes.isError()) {
            warnings.add(exactTeleportRes.dataError().orElseGet(() -> DataError.custom(exactTeleportRes.error().get())));
        } else {
            map.put(ops.createString("exact_teleport"), exactTeleportRes.getOrThrow());
            if (exactTeleportRes.isPartial()) warnings.addAll(exactTeleportRes.warnings());
        }

        DataResult<D> ageRes = Codecs.LONG.encode(ops, value.getAge()).prependPath("age");
        if (ageRes.isError()) {
            warnings.add(ageRes.dataError().orElseGet(() -> DataError.custom(ageRes.error().get())));
        } else {
            map.put(ops.createString("age"), ageRes.getOrThrow());
            if (ageRes.isPartial()) warnings.addAll(ageRes.warnings());
        }

        return warnings.isEmpty() ? DataResult.success(ops.createMap(map)) : DataResult.partial(ops.createMap(map), warnings);
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, TileState base) {
        if (!(base instanceof EndGateway gateway)) return DataResult.success(null);

        return ops.getMap(input)
            .map(DataResult::success)
            .orElseGet(() -> DataResult.error(DataError.typeMismatch("Map", "Unknown")))
            .flatMap(map -> {
                List<DataError> warnings = new ArrayList<>();

                D exitLocData = map.get(ops.createString("exit_location"));
                if (exitLocData != null) {
                    DataResult<org.bukkit.Location> res = Codecs.LOCATION.decode(ops, exitLocData).prependPath("exit_location");
                    if (res.isError()) {
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    } else {
                        gateway.setExitLocation(res.getOrThrow());
                        if (res.isPartial()) warnings.addAll(res.warnings());
                    }
                }

                D exactTeleportData = map.get(ops.createString("exact_teleport"));
                if (exactTeleportData != null) {
                    DataResult<Boolean> res = Codecs.BOOLEAN.decode(ops, exactTeleportData).prependPath("exact_teleport");
                    if (res.isError()) {
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    } else {
                        gateway.setExactTeleport(res.getOrThrow());
                        if (res.isPartial()) warnings.addAll(res.warnings());
                    }
                }

                D ageData = map.get(ops.createString("age"));
                if (ageData != null) {
                    DataResult<Long> res = Codecs.LONG.decode(ops, ageData).prependPath("age");
                    if (res.isError()) {
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    } else {
                        gateway.setAge(res.getOrThrow());
                        if (res.isPartial()) warnings.addAll(res.warnings());
                    }
                }

                return warnings.isEmpty() ? DataResult.success(null) : DataResult.partial(null, warnings);
            });
    }
}