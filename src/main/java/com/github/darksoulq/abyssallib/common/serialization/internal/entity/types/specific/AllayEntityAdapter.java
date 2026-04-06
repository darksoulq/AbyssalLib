package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Allay;
import org.bukkit.entity.Entity;

import java.util.Map;

public class AllayEntityAdapter extends EntityAdapter<Allay> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Allay;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Allay value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("can_duplicate"), Codecs.BOOLEAN.encode(ops, value.canDuplicate()));
        map.put(ops.createString("duplication_cooldown"), Codecs.LONG.encode(ops, value.getDuplicationCooldown()));

        if (value.isDancing()) {
            map.put(ops.createString("is_dancing"), Codecs.BOOLEAN.encode(ops, true));
        }
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Allay allay)) return;

        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("can_duplicate")))).onSuccess(allay::setCanDuplicate);
        Try.of(() -> Codecs.LONG.decode(ops, map.get(ops.createString("duplication_cooldown")))).onSuccess(allay::setDuplicationCooldown);

        D danceData = map.get(ops.createString("is_dancing"));
        if (danceData != null) {
            Try.of(() -> Codecs.BOOLEAN.decode(ops, danceData)).onSuccess(isDancing -> {
                if (isDancing) allay.startDancing();
            });
        }
    }
}