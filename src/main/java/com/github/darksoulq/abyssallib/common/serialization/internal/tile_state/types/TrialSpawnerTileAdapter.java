package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.TileAdapter;
import org.bukkit.NamespacedKey;
import org.bukkit.block.TileState;
import org.bukkit.block.TrialSpawner;
import org.bukkit.loot.LootTable;
import org.bukkit.spawner.TrialSpawnerConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrialSpawnerTileAdapter extends TileAdapter<TrialSpawner> {

    @Override
    public boolean doesApply(TileState state) {
        return state instanceof TrialSpawner;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, TrialSpawner value) {
        Map<D, D> map = new HashMap<>();
        List<DataError> warnings = new ArrayList<>();

        DataResult<D> endRes = Codecs.LONG.encode(ops, value.getCooldownEnd()).prependPath("cooldown_end");
        if (endRes.isError()) warnings.add(endRes.dataError().orElseGet(() -> DataError.custom(endRes.error().get())));
        else map.put(ops.createString("cooldown_end"), endRes.getOrThrow());

        DataResult<D> nextRes = Codecs.LONG.encode(ops, value.getNextSpawnAttempt()).prependPath("next_spawn_attempt");
        if (nextRes.isError())
            warnings.add(nextRes.dataError().orElseGet(() -> DataError.custom(nextRes.error().get())));
        else map.put(ops.createString("next_spawn_attempt"), nextRes.getOrThrow());

        DataResult<D> lengthRes = Codecs.INT.encode(ops, value.getCooldownLength()).prependPath("cooldown_length");
        if (lengthRes.isError())
            warnings.add(lengthRes.dataError().orElseGet(() -> DataError.custom(lengthRes.error().get())));
        else map.put(ops.createString("cooldown_length"), lengthRes.getOrThrow());

        DataResult<D> rangeRes = Codecs.INT.encode(ops, value.getRequiredPlayerRange()).prependPath("required_player_range");
        if (rangeRes.isError())
            warnings.add(rangeRes.dataError().orElseGet(() -> DataError.custom(rangeRes.error().get())));
        else map.put(ops.createString("required_player_range"), rangeRes.getOrThrow());

        DataResult<D> ominousRes = Codecs.BOOLEAN.encode(ops, value.isOminous()).prependPath("is_ominous");
        if (ominousRes.isError())
            warnings.add(ominousRes.dataError().orElseGet(() -> DataError.custom(ominousRes.error().get())));
        else map.put(ops.createString("is_ominous"), ominousRes.getOrThrow());

        TrialSpawnerConfiguration normal = value.getNormalConfiguration();
        if (normal != null) {
            DataResult<D> configRes = serializeConfig(ops, normal).prependPath("normal_config");
            if (configRes.isError())
                warnings.add(configRes.dataError().orElseGet(() -> DataError.custom(configRes.error().get())));
            else {
                map.put(ops.createString("normal_config"), configRes.getOrThrow());
                if (configRes.isPartial()) warnings.addAll(configRes.warnings());
            }
        }

        TrialSpawnerConfiguration ominous = value.getOminousConfiguration();
        if (ominous != null) {
            DataResult<D> configRes = serializeConfig(ops, ominous).prependPath("ominous_config");
            if (configRes.isError())
                warnings.add(configRes.dataError().orElseGet(() -> DataError.custom(configRes.error().get())));
            else {
                map.put(ops.createString("ominous_config"), configRes.getOrThrow());
                if (configRes.isPartial()) warnings.addAll(configRes.warnings());
            }
        }

        return warnings.isEmpty() ? DataResult.success(ops.createMap(map)) : DataResult.partial(ops.createMap(map), warnings);
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, TileState base) {
        if (!(base instanceof TrialSpawner spawner)) return DataResult.success(null);

        return ops.getMap(input)
            .map(DataResult::success)
            .orElseGet(() -> DataResult.error(DataError.typeMismatch("Map", "Unknown")))
            .flatMap(map -> {
                List<DataError> warnings = new ArrayList<>();

                D endData = map.get(ops.createString("cooldown_end"));
                if (endData != null) {
                    DataResult<Long> res = Codecs.LONG.decode(ops, endData).prependPath("cooldown_end");
                    if (res.isError())
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    else spawner.setCooldownEnd(res.getOrThrow());
                }

                D nextData = map.get(ops.createString("next_spawn_attempt"));
                if (nextData != null) {
                    DataResult<Long> res = Codecs.LONG.decode(ops, nextData).prependPath("next_spawn_attempt");
                    if (res.isError())
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    else spawner.setNextSpawnAttempt(res.getOrThrow());
                }

                D lengthData = map.get(ops.createString("cooldown_length"));
                if (lengthData != null) {
                    DataResult<Integer> res = Codecs.INT.decode(ops, lengthData).prependPath("cooldown_length");
                    if (res.isError())
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    else spawner.setCooldownLength(res.getOrThrow());
                }

                D rangeData = map.get(ops.createString("required_player_range"));
                if (rangeData != null) {
                    DataResult<Integer> res = Codecs.INT.decode(ops, rangeData).prependPath("required_player_range");
                    if (res.isError())
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    else spawner.setRequiredPlayerRange(res.getOrThrow());
                }

                D ominousData = map.get(ops.createString("is_ominous"));
                if (ominousData != null) {
                    DataResult<Boolean> res = Codecs.BOOLEAN.decode(ops, ominousData).prependPath("is_ominous");
                    if (res.isError())
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    else spawner.setOminous(res.getOrThrow());
                }

                D normalData = map.get(ops.createString("normal_config"));
                if (normalData != null) {
                    DataResult<Void> res = deserializeConfig(ops, normalData, spawner.getNormalConfiguration()).prependPath("normal_config");
                    if (res.isError())
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    else if (res.isPartial()) warnings.addAll(res.warnings());
                }

                D ominousConfigData = map.get(ops.createString("ominous_config"));
                if (ominousConfigData != null) {
                    DataResult<Void> res = deserializeConfig(ops, ominousConfigData, spawner.getOminousConfiguration()).prependPath("ominous_config");
                    if (res.isError())
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    else if (res.isPartial()) warnings.addAll(res.warnings());
                }

                return warnings.isEmpty() ? DataResult.success(null) : DataResult.partial(null, warnings);
            });
    }

    private <D> DataResult<D> serializeConfig(DynamicOps<D> ops, TrialSpawnerConfiguration config) {
        Map<D, D> map = new HashMap<>();
        List<DataError> warnings = new ArrayList<>();

        DataResult<D> baseSpawnsRes = Codecs.FLOAT.encode(ops, config.getBaseSpawnsBeforeCooldown()).prependPath("base_spawns");
        if (baseSpawnsRes.isError())
            warnings.add(baseSpawnsRes.dataError().orElseGet(() -> DataError.custom(baseSpawnsRes.error().get())));
        else map.put(ops.createString("base_spawns"), baseSpawnsRes.getOrThrow());

        DataResult<D> baseSimRes = Codecs.FLOAT.encode(ops, config.getBaseSimultaneousEntities()).prependPath("base_simultaneous");
        if (baseSimRes.isError())
            warnings.add(baseSimRes.dataError().orElseGet(() -> DataError.custom(baseSimRes.error().get())));
        else map.put(ops.createString("base_simultaneous"), baseSimRes.getOrThrow());

        DataResult<D> addSpawnsRes = Codecs.FLOAT.encode(ops, config.getAdditionalSpawnsBeforeCooldown()).prependPath("additional_spawns");
        if (addSpawnsRes.isError())
            warnings.add(addSpawnsRes.dataError().orElseGet(() -> DataError.custom(addSpawnsRes.error().get())));
        else map.put(ops.createString("additional_spawns"), addSpawnsRes.getOrThrow());

        DataResult<D> addSimRes = Codecs.FLOAT.encode(ops, config.getAdditionalSimultaneousEntities()).prependPath("additional_simultaneous");
        if (addSimRes.isError())
            warnings.add(addSimRes.dataError().orElseGet(() -> DataError.custom(addSimRes.error().get())));
        else map.put(ops.createString("additional_simultaneous"), addSimRes.getOrThrow());

        DataResult<D> delayRes = Codecs.INT.encode(ops, config.getDelay()).prependPath("delay");
        if (delayRes.isError())
            warnings.add(delayRes.dataError().orElseGet(() -> DataError.custom(delayRes.error().get())));
        else map.put(ops.createString("delay"), delayRes.getOrThrow());

        DataResult<D> reqRangeRes = Codecs.INT.encode(ops, config.getRequiredPlayerRange()).prependPath("required_player_range");
        if (reqRangeRes.isError())
            warnings.add(reqRangeRes.dataError().orElseGet(() -> DataError.custom(reqRangeRes.error().get())));
        else map.put(ops.createString("required_player_range"), reqRangeRes.getOrThrow());

        DataResult<D> spawnRangeRes = Codecs.INT.encode(ops, config.getSpawnRange()).prependPath("spawn_range");
        if (spawnRangeRes.isError())
            warnings.add(spawnRangeRes.dataError().orElseGet(() -> DataError.custom(spawnRangeRes.error().get())));
        else map.put(ops.createString("spawn_range"), spawnRangeRes.getOrThrow());

        if (config.getSpawnedType() != null) {
            DataResult<D> typeRes = ExtraCodecs.ENTITY_TYPE.encode(ops, config.getSpawnedType()).prependPath("spawned_type");
            if (typeRes.isError())
                warnings.add(typeRes.dataError().orElseGet(() -> DataError.custom(typeRes.error().get())));
            else map.put(ops.createString("spawned_type"), typeRes.getOrThrow());
        }

        Map<LootTable, Integer> rewards = config.getPossibleRewards();
        if (!rewards.isEmpty()) {
            Map<D, D> rewardsMap = new HashMap<>();
            for (Map.Entry<LootTable, Integer> entry : rewards.entrySet()) {
                DataResult<D> keyRes = Codecs.NAMESPACED_KEY.encode(ops, entry.getKey().getKey());
                DataResult<D> valRes = Codecs.INT.encode(ops, entry.getValue());

                if (keyRes.isSuccess() && valRes.isSuccess()) {
                    rewardsMap.put(keyRes.getOrThrow(), valRes.getOrThrow());
                }
            }
            map.put(ops.createString("rewards"), ops.createMap(rewardsMap));
        }

        return warnings.isEmpty() ? DataResult.success(ops.createMap(map)) : DataResult.partial(ops.createMap(map), warnings);
    }

    private <D> DataResult<Void> deserializeConfig(DynamicOps<D> ops, D input, TrialSpawnerConfiguration config) {
        if (config == null) return DataResult.success(null);

        return ops.getMap(input)
            .map(DataResult::success)
            .orElseGet(() -> DataResult.error(DataError.typeMismatch("Map", "Unknown")))
            .flatMap(map -> {
                List<DataError> warnings = new ArrayList<>();

                D baseSpawns = map.get(ops.createString("base_spawns"));
                if (baseSpawns != null) {
                    DataResult<Float> res = Codecs.FLOAT.decode(ops, baseSpawns).prependPath("base_spawns");
                    if (res.isError())
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    else config.setBaseSpawnsBeforeCooldown(res.getOrThrow());
                }

                D baseSim = map.get(ops.createString("base_simultaneous"));
                if (baseSim != null) {
                    DataResult<Float> res = Codecs.FLOAT.decode(ops, baseSim).prependPath("base_simultaneous");
                    if (res.isError())
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    else config.setBaseSimultaneousEntities(res.getOrThrow());
                }

                D addSpawns = map.get(ops.createString("additional_spawns"));
                if (addSpawns != null) {
                    DataResult<Float> res = Codecs.FLOAT.decode(ops, addSpawns).prependPath("additional_spawns");
                    if (res.isError())
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    else config.setAdditionalSpawnsBeforeCooldown(res.getOrThrow());
                }

                D addSim = map.get(ops.createString("additional_simultaneous"));
                if (addSim != null) {
                    DataResult<Float> res = Codecs.FLOAT.decode(ops, addSim).prependPath("additional_simultaneous");
                    if (res.isError())
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    else config.setAdditionalSimultaneousEntities(res.getOrThrow());
                }

                D delay = map.get(ops.createString("delay"));
                if (delay != null) {
                    DataResult<Integer> res = Codecs.INT.decode(ops, delay).prependPath("delay");
                    if (res.isError())
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    else config.setDelay(res.getOrThrow());
                }

                D reqRange = map.get(ops.createString("required_player_range"));
                if (reqRange != null) {
                    DataResult<Integer> res = Codecs.INT.decode(ops, reqRange).prependPath("required_player_range");
                    if (res.isError())
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    else config.setRequiredPlayerRange(res.getOrThrow());
                }

                D spawnRange = map.get(ops.createString("spawn_range"));
                if (spawnRange != null) {
                    DataResult<Integer> res = Codecs.INT.decode(ops, spawnRange).prependPath("spawn_range");
                    if (res.isError())
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    else config.setSpawnRange(res.getOrThrow());
                }

                D spawnedType = map.get(ops.createString("spawned_type"));
                if (spawnedType != null) {
                    DataResult<org.bukkit.entity.EntityType> res = ExtraCodecs.ENTITY_TYPE.decode(ops, spawnedType).prependPath("spawned_type");
                    if (res.isError())
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    else config.setSpawnedType(res.getOrThrow());
                }

                D rewardsData = map.get(ops.createString("rewards"));
                if (rewardsData != null) {
                    Map<D, D> rewardsMap = ops.getMap(rewardsData).orElse(new HashMap<>());
                    Map<LootTable, Integer> finalRewards = new HashMap<>();
                    for (Map.Entry<D, D> entry : rewardsMap.entrySet()) {
                        DataResult<NamespacedKey> keyRes = Codecs.NAMESPACED_KEY.decode(ops, entry.getKey());
                        DataResult<Integer> valRes = Codecs.INT.decode(ops, entry.getValue());

                        if (keyRes.isSuccess() && valRes.isSuccess()) {
                            LootTable table = org.bukkit.Bukkit.getLootTable(keyRes.getOrThrow());
                            if (table != null) {
                                finalRewards.put(table, valRes.getOrThrow());
                            }
                        }
                    }
                    if (!finalRewards.isEmpty()) {
                        config.setPossibleRewards(finalRewards);
                    }
                }

                return warnings.isEmpty() ? DataResult.success(null) : DataResult.partial(null, warnings);
            });
    }
}