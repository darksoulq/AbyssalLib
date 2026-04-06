package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import io.papermc.paper.entity.Leashable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import java.util.Map;

public class LeashableEntityAdapter extends EntityAdapter<Leashable> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Leashable;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Leashable value, Map<D, D> map) throws Codec.CodecException {
        if (value.isLeashed()) {
            map.put(ops.createString("leash_holder_uuid"), Codecs.UUID.encode(ops, value.getLeashHolder().getUniqueId()));
        }
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Leashable leashable)) return;

        D holderData = map.get(ops.createString("leash_holder_uuid"));
        if (holderData != null) {
            Try.of(() -> Codecs.UUID.decode(ops, holderData)).onSuccess(uuid -> {
                Entity holder = Bukkit.getEntity(uuid);
                if (holder != null) leashable.setLeashHolder(holder);
            });
        }
    }
}