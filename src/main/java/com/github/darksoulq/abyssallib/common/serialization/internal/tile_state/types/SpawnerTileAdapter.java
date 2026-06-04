package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.TileAdapter;
import org.bukkit.block.TileState;
import org.bukkit.entity.EntityType;
import org.bukkit.spawner.Spawner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpawnerTileAdapter extends TileAdapter<Spawner> {

    @Override
    public boolean doesApply(TileState state) {
        return state instanceof Spawner;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, Spawner value) {
        Map<D, D> map = new HashMap<>();
        List<DataError> warnings = new ArrayList<>();

        DataResult<D> delayRes = Codecs.INT.encode(ops, value.getDelay()).prependPath("delay");
        if (delayRes.isError())
            warnings.add(delayRes.dataError().orElseGet(() -> DataError.custom(delayRes.error().get())));
        else map.put(ops.createString("delay"), delayRes.getOrThrow());

        DataResult<D> minDelayRes = Codecs.INT.encode(ops, value.getMinSpawnDelay()).prependPath("min_spawn_delay");
        if (minDelayRes.isError())
            warnings.add(minDelayRes.dataError().orElseGet(() -> DataError.custom(minDelayRes.error().get())));
        else map.put(ops.createString("min_spawn_delay"), minDelayRes.getOrThrow());

        DataResult<D> maxDelayRes = Codecs.INT.encode(ops, value.getMaxSpawnDelay()).prependPath("max_spawn_delay");
        if (maxDelayRes.isError())
            warnings.add(maxDelayRes.dataError().orElseGet(() -> DataError.custom(maxDelayRes.error().get())));
        else map.put(ops.createString("max_spawn_delay"), maxDelayRes.getOrThrow());

        DataResult<D> countRes = Codecs.INT.encode(ops, value.getSpawnCount()).prependPath("spawn_count");
        if (countRes.isError())
            warnings.add(countRes.dataError().orElseGet(() -> DataError.custom(countRes.error().get())));
        else map.put(ops.createString("spawn_count"), countRes.getOrThrow());

        DataResult<D> nearbyRes = Codecs.INT.encode(ops, value.getMaxNearbyEntities()).prependPath("max_nearby_entities");
        if (nearbyRes.isError())
            warnings.add(nearbyRes.dataError().orElseGet(() -> DataError.custom(nearbyRes.error().get())));
        else map.put(ops.createString("max_nearby_entities"), nearbyRes.getOrThrow());

        DataResult<D> reqRangeRes = Codecs.INT.encode(ops, value.getRequiredPlayerRange()).prependPath("required_player_range");
        if (reqRangeRes.isError())
            warnings.add(reqRangeRes.dataError().orElseGet(() -> DataError.custom(reqRangeRes.error().get())));
        else map.put(ops.createString("required_player_range"), reqRangeRes.getOrThrow());

        DataResult<D> spawnRangeRes = Codecs.INT.encode(ops, value.getSpawnRange()).prependPath("spawn_range");
        if (spawnRangeRes.isError())
            warnings.add(spawnRangeRes.dataError().orElseGet(() -> DataError.custom(spawnRangeRes.error().get())));
        else map.put(ops.createString("spawn_range"), spawnRangeRes.getOrThrow());

        if (value.getSpawnedType() != null) {
            DataResult<D> typeRes = ExtraCodecs.ENTITY_TYPE.encode(ops, value.getSpawnedType()).prependPath("spawned_type");
            if (typeRes.isError())
                warnings.add(typeRes.dataError().orElseGet(() -> DataError.custom(typeRes.error().get())));
            else map.put(ops.createString("spawned_type"), typeRes.getOrThrow());
        }

        return warnings.isEmpty() ? DataResult.success(ops.createMap(map)) : DataResult.partial(ops.createMap(map), warnings);
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, TileState base) {
        if (!(base instanceof Spawner spawner)) return DataResult.success(null);

        return ops.getMap(input)
            .map(DataResult::success)
            .orElseGet(() -> DataResult.error(DataError.typeMismatch("Map", "Unknown")))
            .flatMap(map -> {
                List<DataError> warnings = new ArrayList<>();

                D delay = map.get(ops.createString("delay"));
                if (delay != null) {
                    DataResult<Integer> res = Codecs.INT.decode(ops, delay).prependPath("delay");
                    if (res.isError())
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    else spawner.setDelay(res.getOrThrow());
                }

                D minDelay = map.get(ops.createString("min_spawn_delay"));
                if (minDelay != null) {
                    DataResult<Integer> res = Codecs.INT.decode(ops, minDelay).prependPath("min_spawn_delay");
                    if (res.isError())
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    else spawner.setMinSpawnDelay(res.getOrThrow());
                }

                D maxDelay = map.get(ops.createString("max_spawn_delay"));
                if (maxDelay != null) {
                    DataResult<Integer> res = Codecs.INT.decode(ops, maxDelay).prependPath("max_spawn_delay");
                    if (res.isError())
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    else spawner.setMaxSpawnDelay(res.getOrThrow());
                }

                D spawnCount = map.get(ops.createString("spawn_count"));
                if (spawnCount != null) {
                    DataResult<Integer> res = Codecs.INT.decode(ops, spawnCount).prependPath("spawn_count");
                    if (res.isError())
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    else spawner.setSpawnCount(res.getOrThrow());
                }

                D maxNearby = map.get(ops.createString("max_nearby_entities"));
                if (maxNearby != null) {
                    DataResult<Integer> res = Codecs.INT.decode(ops, maxNearby).prependPath("max_nearby_entities");
                    if (res.isError())
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    else spawner.setMaxNearbyEntities(res.getOrThrow());
                }

                D reqRange = map.get(ops.createString("required_player_range"));
                if (reqRange != null) {
                    DataResult<Integer> res = Codecs.INT.decode(ops, reqRange).prependPath("required_player_range");
                    if (res.isError())
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    else spawner.setRequiredPlayerRange(res.getOrThrow());
                }

                D spawnRange = map.get(ops.createString("spawn_range"));
                if (spawnRange != null) {
                    DataResult<Integer> res = Codecs.INT.decode(ops, spawnRange).prependPath("spawn_range");
                    if (res.isError())
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    else spawner.setSpawnRange(res.getOrThrow());
                }

                D spawnedType = map.get(ops.createString("spawned_type"));
                if (spawnedType != null) {
                    DataResult<EntityType> res = ExtraCodecs.ENTITY_TYPE.decode(ops, spawnedType).prependPath("spawned_type");
                    if (res.isError())
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    else spawner.setSpawnedType(res.getOrThrow());
                }

                return warnings.isEmpty() ? DataResult.success(null) : DataResult.partial(null, warnings);
            });
    }
}