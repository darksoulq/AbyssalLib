package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.TileAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.block.EndGateway;
import org.bukkit.block.TileState;

import java.util.HashMap;
import java.util.Map;

public class EndGatewayTileAdapter extends TileAdapter<EndGateway> {

    @Override
    public boolean doesApply(TileState state) {
        return state instanceof EndGateway;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, EndGateway value) throws Codec.CodecException {
        Map<D, D> map = new HashMap<>();

        if (value.getExitLocation() != null) {
            map.put(ops.createString("exit_location"), Codecs.LOCATION.encode(ops, value.getExitLocation()));
        }
        map.put(ops.createString("exact_teleport"), Codecs.BOOLEAN.encode(ops, value.isExactTeleport()));
        map.put(ops.createString("age"), Codecs.LONG.encode(ops, value.getAge()));

        return ops.createMap(map);
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, TileState base) throws Codec.CodecException {
        if (!(base instanceof EndGateway gateway)) return;
        Map<D, D> map = ops.getMap(input).orElseThrow(() -> new Codec.CodecException("Expected map for EndGateway"));

        D exitLocData = map.get(ops.createString("exit_location"));
        if (exitLocData != null) {
            Try.of(() -> Codecs.LOCATION.decode(ops, exitLocData)).onSuccess(gateway::setExitLocation);
        }

        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("exact_teleport")))).onSuccess(gateway::setExactTeleport);
        Try.of(() -> Codecs.LONG.decode(ops, map.get(ops.createString("age")))).onSuccess(gateway::setAge);
    }
}