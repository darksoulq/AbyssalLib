package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.TileAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.block.TileState;
import org.bukkit.spawner.Spawner;

import java.util.HashMap;
import java.util.Map;

public class SpawnerTileAdapter extends TileAdapter<Spawner> {

    @Override
    public boolean doesApply(TileState state) {
        return state instanceof Spawner;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, Spawner value) throws Codec.CodecException {
        Map<D, D> map = new HashMap<>();

        map.put(ops.createString("delay"), Codecs.INT.encode(ops, value.getDelay()));
        map.put(ops.createString("min_spawn_delay"), Codecs.INT.encode(ops, value.getMinSpawnDelay()));
        map.put(ops.createString("max_spawn_delay"), Codecs.INT.encode(ops, value.getMaxSpawnDelay()));
        map.put(ops.createString("spawn_count"), Codecs.INT.encode(ops, value.getSpawnCount()));
        map.put(ops.createString("max_nearby_entities"), Codecs.INT.encode(ops, value.getMaxNearbyEntities()));
        map.put(ops.createString("required_player_range"), Codecs.INT.encode(ops, value.getRequiredPlayerRange()));
        map.put(ops.createString("spawn_range"), Codecs.INT.encode(ops, value.getSpawnRange()));

        if (value.getSpawnedType() != null) {
            map.put(ops.createString("spawned_type"), ExtraCodecs.ENTITY_TYPE.encode(ops, value.getSpawnedType()));
        }

        return ops.createMap(map);
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, TileState base) throws Codec.CodecException {
        if (!(base instanceof Spawner spawner)) return;
        Map<D, D> map = ops.getMap(input).orElseThrow(() -> new Codec.CodecException("Expected map for Spawner"));

        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("delay")))).onSuccess(spawner::setDelay);
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("min_spawn_delay")))).onSuccess(spawner::setMinSpawnDelay);
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("max_spawn_delay")))).onSuccess(spawner::setMaxSpawnDelay);
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("spawn_count")))).onSuccess(spawner::setSpawnCount);
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("max_nearby_entities")))).onSuccess(spawner::setMaxNearbyEntities);
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("required_player_range")))).onSuccess(spawner::setRequiredPlayerRange);
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("spawn_range")))).onSuccess(spawner::setSpawnRange);

        D spawnedTypeData = map.get(ops.createString("spawned_type"));
        if (spawnedTypeData != null) {
            Try.of(() -> ExtraCodecs.ENTITY_TYPE.decode(ops, spawnedTypeData)).onSuccess(spawner::setSpawnedType);
        }
    }
}