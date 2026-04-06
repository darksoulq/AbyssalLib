package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Tameable;

import java.util.Map;

public class TameableEntityAdapter extends EntityAdapter<Tameable> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Tameable;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Tameable value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("is_tamed"), Codecs.BOOLEAN.encode(ops, value.isTamed()));
        if (value.getOwnerUniqueId() != null) {
            map.put(ops.createString("owner_uuid"), Codecs.UUID.encode(ops, value.getOwnerUniqueId()));
        }
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Tameable tameable)) return;
        
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("is_tamed")))).onSuccess(tameable::setTamed);
        
        D ownerData = map.get(ops.createString("owner_uuid"));
        if (ownerData != null) {
            Try.of(() -> Codecs.UUID.decode(ops, ownerData)).onSuccess(uuid -> tameable.setOwner(Bukkit.getOfflinePlayer(uuid)));
        }
    }
}