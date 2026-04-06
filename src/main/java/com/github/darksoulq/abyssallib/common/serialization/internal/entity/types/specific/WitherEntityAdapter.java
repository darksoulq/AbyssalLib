package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Wither;

import java.util.Map;

public class WitherEntityAdapter extends EntityAdapter<Wither> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Wither;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Wither value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("invulnerable_ticks"), Codecs.INT.encode(ops, value.getInvulnerableTicks()));
        map.put(ops.createString("can_travel_through_portals"), Codecs.BOOLEAN.encode(ops, value.canTravelThroughPortals()));

        if (value.getTarget(Wither.Head.CENTER) != null) {
            map.put(ops.createString("target_center"), Codecs.UUID.encode(ops, value.getTarget(Wither.Head.CENTER).getUniqueId()));
        }
        if (value.getTarget(Wither.Head.LEFT) != null) {
            map.put(ops.createString("target_left"), Codecs.UUID.encode(ops, value.getTarget(Wither.Head.LEFT).getUniqueId()));
        }
        if (value.getTarget(Wither.Head.RIGHT) != null) {
            map.put(ops.createString("target_right"), Codecs.UUID.encode(ops, value.getTarget(Wither.Head.RIGHT).getUniqueId()));
        }
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Wither wither)) return;

        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("invulnerable_ticks")))).onSuccess(wither::setInvulnerableTicks);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("can_travel_through_portals")))).onSuccess(wither::setCanTravelThroughPortals);

        D centerTargetData = map.get(ops.createString("target_center"));
        if (centerTargetData != null) {
            Try.of(() -> Codecs.UUID.decode(ops, centerTargetData)).onSuccess(uuid -> {
                Entity target = Bukkit.getEntity(uuid);
                if (target instanceof LivingEntity livingTarget) wither.setTarget(Wither.Head.CENTER, livingTarget);
            });
        }

        D leftTargetData = map.get(ops.createString("target_left"));
        if (leftTargetData != null) {
            Try.of(() -> Codecs.UUID.decode(ops, leftTargetData)).onSuccess(uuid -> {
                Entity target = Bukkit.getEntity(uuid);
                if (target instanceof LivingEntity livingTarget) wither.setTarget(Wither.Head.LEFT, livingTarget);
            });
        }

        D rightTargetData = map.get(ops.createString("target_right"));
        if (rightTargetData != null) {
            Try.of(() -> Codecs.UUID.decode(ops, rightTargetData)).onSuccess(uuid -> {
                Entity target = Bukkit.getEntity(uuid);
                if (target instanceof LivingEntity livingTarget) wither.setTarget(Wither.Head.RIGHT, livingTarget);
            });
        }
    }
}