package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
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
    public <D> DataResult<D> serialize(DynamicOps<D> ops, Conduit value) {
        Map<D, D> map = new HashMap<>();

        if (value.getTarget() != null) {
            DataResult<D> res = Codecs.UUID.encode(ops, value.getTarget().getUniqueId()).prependPath("target");
            if (res.isError()) return DataResult.error(res.error().get());

            map.put(ops.createString("target"), res.getOrThrow());
            return res.isPartial() ? DataResult.partial(ops.createMap(map), res.warnings()) : DataResult.success(ops.createMap(map));
        }

        return DataResult.success(ops.createMap(map));
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, TileState base) {
        if (!(base instanceof Conduit conduit)) return DataResult.success(null);

        return ops.getMap(input)
            .map(DataResult::success)
            .orElseGet(() -> DataResult.error(DataError.typeMismatch("Map", "Unknown")))
            .flatMap(map -> {
                D targetData = map.get(ops.createString("target"));
                if (targetData != null) {
                    DataResult<UUID> res = Codecs.UUID.decode(ops, targetData).prependPath("target");
                    if (res.isError()) return DataResult.error(res.error().get());

                    Entity entity = Bukkit.getEntity(res.getOrThrow());
                    if (entity instanceof LivingEntity living) {
                        conduit.setTarget(living);
                    }

                    return res.isPartial() ? DataResult.partial(null, res.warnings()) : DataResult.success(null);
                }
                return DataResult.success(null);
            });
    }
}