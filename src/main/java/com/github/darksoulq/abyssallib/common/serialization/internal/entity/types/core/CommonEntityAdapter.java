package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.core;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.Map;

public class CommonEntityAdapter extends EntityAdapter<Entity> {

    @Override
    public boolean doesApply(Entity entity) { return true; }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Entity value, Map<D, D> map) throws Codec.CodecException {
        if (value.customName() != null) map.put(ops.createString("custom_name"), Codecs.TEXT_COMPONENT.encode(ops, value.customName()));
        map.put(ops.createString("custom_name_visible"), Codecs.BOOLEAN.encode(ops, value.isCustomNameVisible()));
        map.put(ops.createString("glowing"), Codecs.BOOLEAN.encode(ops, value.isGlowing()));
        map.put(ops.createString("gravity"), Codecs.BOOLEAN.encode(ops, value.hasGravity()));
        map.put(ops.createString("invulnerable"), Codecs.BOOLEAN.encode(ops, value.isInvulnerable()));
        map.put(ops.createString("silent"), Codecs.BOOLEAN.encode(ops, value.isSilent()));
        map.put(ops.createString("fire_ticks"), Codecs.INT.encode(ops, value.getFireTicks()));
        map.put(ops.createString("ticks_lived"), Codecs.INT.encode(ops, value.getTicksLived()));
        map.put(ops.createString("scoreboard_tags"), Codecs.STRING.list().encode(ops, new ArrayList<>(value.getScoreboardTags())));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        D name = map.get(ops.createString("custom_name"));
        if (name != null) Try.of(() -> Codecs.TEXT_COMPONENT.decode(ops, name)).onSuccess(base::customName);

        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("custom_name_visible")))).onSuccess(base::setCustomNameVisible);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("glowing")))).onSuccess(base::setGlowing);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("gravity")))).onSuccess(base::setGravity);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("invulnerable")))).onSuccess(base::setInvulnerable);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("silent")))).onSuccess(base::setSilent);
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("fire_ticks")))).onSuccess(base::setFireTicks);
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("ticks_lived")))).onSuccess(base::setTicksLived);

        D tags = map.get(ops.createString("scoreboard_tags"));
        if (tags != null) {
            Try.of(() -> Codecs.STRING.list().decode(ops, tags)).onSuccess(t -> {
                base.getScoreboardTags().clear();
                base.getScoreboardTags().addAll(t);
            });
        }
    }
}