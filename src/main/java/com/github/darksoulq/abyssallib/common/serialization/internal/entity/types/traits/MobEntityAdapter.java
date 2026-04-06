package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import net.kyori.adventure.util.TriState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;

import java.util.Map;

public class MobEntityAdapter extends EntityAdapter<Mob> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Mob;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Mob value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("despawn_peaceful_override"), Codecs.STRING.encode(ops, value.getDespawnInPeacefulOverride().name()));
        map.put(ops.createString("aware"), Codecs.BOOLEAN.encode(ops, value.isAware()));
        map.put(ops.createString("aggressive"), Codecs.BOOLEAN.encode(ops, value.isAggressive()));
        map.put(ops.createString("left_handed"), Codecs.BOOLEAN.encode(ops, value.isLeftHanded()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Mob mob)) return;

        Try.of(() -> Codecs.STRING.decode(ops, map.get(ops.createString("despawn_peaceful_override")))).onSuccess(s -> mob.setDespawnInPeacefulOverride(TriState.valueOf(s)));
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("aware")))).onSuccess(mob::setAware);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("aggressive")))).onSuccess(mob::setAggressive);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("left_handed")))).onSuccess(mob::setLeftHanded);
    }
}