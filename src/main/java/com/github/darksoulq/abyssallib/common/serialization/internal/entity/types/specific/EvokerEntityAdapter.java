package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Evoker;
import org.bukkit.entity.Sheep;

import java.util.Map;

public class EvokerEntityAdapter extends EntityAdapter<Evoker> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Evoker;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Evoker value, Map<D, D> map) throws Codec.CodecException {
        if (value.getWololoTarget() != null) {
            map.put(ops.createString("wololo_target_uuid"), Codecs.UUID.encode(ops, value.getWololoTarget().getUniqueId()));
        }
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Evoker evoker)) return;

        D targetData = map.get(ops.createString("wololo_target_uuid"));
        if (targetData != null) {
            Try.of(() -> Codecs.UUID.decode(ops, targetData)).onSuccess(uuid -> {
                Entity target = Bukkit.getEntity(uuid);
                if (target instanceof Sheep sheep) {
                    evoker.setWololoTarget(sheep);
                }
            });
        }
    }
}