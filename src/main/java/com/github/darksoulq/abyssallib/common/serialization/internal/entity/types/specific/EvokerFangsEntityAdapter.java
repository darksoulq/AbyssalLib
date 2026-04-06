package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EvokerFangs;
import org.bukkit.entity.LivingEntity;

import java.util.Map;

public class EvokerFangsEntityAdapter extends EntityAdapter<EvokerFangs> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof EvokerFangs;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, EvokerFangs value, Map<D, D> map) throws Codec.CodecException {
        if (value.getOwner() != null) {
            map.put(ops.createString("owner_uuid"), Codecs.UUID.encode(ops, value.getOwner().getUniqueId()));
        }
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof EvokerFangs fangs)) return;

        D ownerData = map.get(ops.createString("owner_uuid"));
        if (ownerData != null) {
            Try.of(() -> Codecs.UUID.decode(ops, ownerData)).onSuccess(uuid -> {
                Entity owner = Bukkit.getEntity(uuid);
                if (owner instanceof LivingEntity livingOwner) {
                    fangs.setOwner(livingOwner);
                }
            });
        }
    }
}