package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.TileAdapter;
import org.bukkit.Bukkit;
import org.bukkit.block.Conduit;
import org.bukkit.block.TileState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ConduitTileAdapter extends TileAdapter<Conduit> {

    @Override
    public boolean doesApply(TileState state) {
        return state instanceof Conduit;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, Conduit value) throws Codec.CodecException {
        Map<D, D> map = new HashMap<>();
        if (value.getTarget() != null) {
            map.put(ops.createString("target"), Codecs.UUID.encode(ops, value.getTarget().getUniqueId()));
        }
        return ops.createMap(map);
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, TileState base) throws Codec.CodecException {
        if (!(base instanceof Conduit conduit)) return;
        Map<D, D> map = ops.getMap(input).orElseThrow(() -> new Codec.CodecException("Expected map for Conduit"));

        D targetData = map.get(ops.createString("target"));
        if (targetData != null) {
            UUID uuid = Codecs.UUID.decode(ops, targetData);
            Entity entity = Bukkit.getEntity(uuid);
            if (entity instanceof LivingEntity living) {
                conduit.setTarget(living);
            }
        }
    }
}