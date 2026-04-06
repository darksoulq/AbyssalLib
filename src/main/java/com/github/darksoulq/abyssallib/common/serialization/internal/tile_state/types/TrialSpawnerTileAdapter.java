package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.TileAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.NamespacedKey;
import org.bukkit.block.TileState;
import org.bukkit.block.TrialSpawner;
import org.bukkit.loot.LootTable;
import org.bukkit.spawner.TrialSpawnerConfiguration;

import java.util.HashMap;
import java.util.Map;

public class TrialSpawnerTileAdapter extends TileAdapter<TrialSpawner> {

    @Override
    public boolean doesApply(TileState state) {
        return state instanceof TrialSpawner;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, TrialSpawner value) throws Codec.CodecException {
        Map<D, D> map = new HashMap<>();

        map.put(ops.createString("cooldown_end"), Codecs.LONG.encode(ops, value.getCooldownEnd()));
        map.put(ops.createString("next_spawn_attempt"), Codecs.LONG.encode(ops, value.getNextSpawnAttempt()));
        map.put(ops.createString("cooldown_length"), Codecs.INT.encode(ops, value.getCooldownLength()));
        map.put(ops.createString("required_player_range"), Codecs.INT.encode(ops, value.getRequiredPlayerRange()));
        map.put(ops.createString("is_ominous"), Codecs.BOOLEAN.encode(ops, value.isOminous()));

        Try.run(() -> {
            TrialSpawnerConfiguration normal = value.getNormalConfiguration();
            if (normal != null) {
                map.put(ops.createString("normal_config"), serializeConfig(ops, normal));
            }
        });

        Try.run(() -> {
            TrialSpawnerConfiguration ominous = value.getOminousConfiguration();
            if (ominous != null) {
                map.put(ops.createString("ominous_config"), serializeConfig(ops, ominous));
            }
        });

        return ops.createMap(map);
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, TileState base) throws Codec.CodecException {
        if (!(base instanceof TrialSpawner spawner)) return;
        Map<D, D> map = ops.getMap(input).orElseThrow(() -> new Codec.CodecException("Expected map for TrialSpawner"));

        Try.of(() -> Codecs.LONG.decode(ops, map.get(ops.createString("cooldown_end")))).onSuccess(spawner::setCooldownEnd);
        Try.of(() -> Codecs.LONG.decode(ops, map.get(ops.createString("next_spawn_attempt")))).onSuccess(spawner::setNextSpawnAttempt);
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("cooldown_length")))).onSuccess(spawner::setCooldownLength);
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("required_player_range")))).onSuccess(spawner::setRequiredPlayerRange);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("is_ominous")))).onSuccess(spawner::setOminous);

        D normalData = map.get(ops.createString("normal_config"));
        if (normalData != null) {
            Try.run(() -> deserializeConfig(ops, normalData, spawner.getNormalConfiguration()));
        }

        D ominousData = map.get(ops.createString("ominous_config"));
        if (ominousData != null) {
            Try.run(() -> deserializeConfig(ops, ominousData, spawner.getOminousConfiguration()));
        }
    }

    private <D> D serializeConfig(DynamicOps<D> ops, TrialSpawnerConfiguration config) throws Codec.CodecException {
        Map<D, D> map = new HashMap<>();

        map.put(ops.createString("base_spawns"), Codecs.FLOAT.encode(ops, config.getBaseSpawnsBeforeCooldown()));
        map.put(ops.createString("base_simultaneous"), Codecs.FLOAT.encode(ops, config.getBaseSimultaneousEntities()));
        map.put(ops.createString("additional_spawns"), Codecs.FLOAT.encode(ops, config.getAdditionalSpawnsBeforeCooldown()));
        map.put(ops.createString("additional_simultaneous"), Codecs.FLOAT.encode(ops, config.getAdditionalSimultaneousEntities()));

        map.put(ops.createString("delay"), Codecs.INT.encode(ops, config.getDelay()));
        map.put(ops.createString("required_player_range"), Codecs.INT.encode(ops, config.getRequiredPlayerRange()));
        map.put(ops.createString("spawn_range"), Codecs.INT.encode(ops, config.getSpawnRange()));

        if (config.getSpawnedType() != null) {
            map.put(ops.createString("spawned_type"), ExtraCodecs.ENTITY_TYPE.encode(ops, config.getSpawnedType()));
        }

        Map<LootTable, Integer> rewards = config.getPossibleRewards();
        if (!rewards.isEmpty()) {
            Map<D, D> rewardsMap = new HashMap<>();
            for (Map.Entry<LootTable, Integer> entry : rewards.entrySet()) {
                rewardsMap.put(Codecs.NAMESPACED_KEY.encode(ops, entry.getKey().getKey()), Codecs.INT.encode(ops, entry.getValue()));
            }
            map.put(ops.createString("rewards"), ops.createMap(rewardsMap));
        }

        return ops.createMap(map);
    }

    private <D> void deserializeConfig(DynamicOps<D> ops, D input, TrialSpawnerConfiguration config) throws Codec.CodecException {
        if (config == null) return;
        Map<D, D> map = ops.getMap(input).orElseThrow(() -> new Codec.CodecException("Expected map for TrialSpawnerConfig"));

        Try.of(() -> Codecs.FLOAT.decode(ops, map.get(ops.createString("base_spawns")))).onSuccess(config::setBaseSpawnsBeforeCooldown);
        Try.of(() -> Codecs.FLOAT.decode(ops, map.get(ops.createString("base_simultaneous")))).onSuccess(config::setBaseSimultaneousEntities);
        Try.of(() -> Codecs.FLOAT.decode(ops, map.get(ops.createString("additional_spawns")))).onSuccess(config::setAdditionalSpawnsBeforeCooldown);
        Try.of(() -> Codecs.FLOAT.decode(ops, map.get(ops.createString("additional_simultaneous")))).onSuccess(config::setAdditionalSimultaneousEntities);

        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("delay")))).onSuccess(config::setDelay);
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("required_player_range")))).onSuccess(config::setRequiredPlayerRange);
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("spawn_range")))).onSuccess(config::setSpawnRange);

        D spawnedTypeData = map.get(ops.createString("spawned_type"));
        if (spawnedTypeData != null) {
            Try.of(() -> ExtraCodecs.ENTITY_TYPE.decode(ops, spawnedTypeData)).onSuccess(config::setSpawnedType);
        }

        D rewardsData = map.get(ops.createString("rewards"));
        if (rewardsData != null) {
            Map<D, D> rewardsMap = ops.getMap(rewardsData).orElse(new HashMap<>());
            Map<LootTable, Integer> finalRewards = new HashMap<>();
            for (Map.Entry<D, D> entry : rewardsMap.entrySet()) {
                NamespacedKey key = Codecs.NAMESPACED_KEY.decode(ops, entry.getKey());
                LootTable table = org.bukkit.Bukkit.getLootTable(key);
                if (table != null) {
                    finalRewards.put(table, Codecs.INT.decode(ops, entry.getValue()));
                }
            }
            if (!finalRewards.isEmpty()) {
                config.setPossibleRewards(finalRewards);
            }
        }
    }
}