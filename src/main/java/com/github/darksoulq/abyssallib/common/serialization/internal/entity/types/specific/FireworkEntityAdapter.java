package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.List;
import java.util.Map;

public class FireworkEntityAdapter extends EntityAdapter<Firework> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Firework;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Firework value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("is_shot_at_angle"), Codecs.BOOLEAN.encode(ops, value.isShotAtAngle()));

        FireworkMeta meta = value.getFireworkMeta();
        map.put(ops.createString("power"), Codecs.INT.encode(ops, meta.getPower()));
        if (meta.hasEffects()) {
            map.put(ops.createString("effects"), ExtraCodecs.FIREWORK_EFFECT.list().encode(ops, meta.getEffects()));
        }
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Firework firework)) return;

        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("is_shot_at_angle")))).onSuccess(firework::setShotAtAngle);

        FireworkMeta meta = firework.getFireworkMeta();
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("power")))).onSuccess(meta::setPower);

        D effectsData = map.get(ops.createString("effects"));
        if (effectsData != null) {
            Try.of(() -> ExtraCodecs.FIREWORK_EFFECT.list().decode(ops, effectsData)).onSuccess(effects -> {
                for (FireworkEffect effect : effects) {
                    meta.addEffect(effect);
                }
            });
        }
        firework.setFireworkMeta(meta);
    }
}