package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Entity;
import org.bukkit.potion.PotionType;

import java.util.Map;

public class AreaEffectCloudEntityAdapter extends EntityAdapter<AreaEffectCloud> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof AreaEffectCloud;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, AreaEffectCloud value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("duration"), Codecs.INT.encode(ops, value.getDuration()));
        map.put(ops.createString("duration_on_use"), Codecs.INT.encode(ops, value.getDurationOnUse()));
        map.put(ops.createString("radius"), Codecs.FLOAT.encode(ops, value.getRadius()));
        map.put(ops.createString("radius_on_use"), Codecs.FLOAT.encode(ops, value.getRadiusOnUse()));
        map.put(ops.createString("radius_per_tick"), Codecs.FLOAT.encode(ops, value.getRadiusPerTick()));
        map.put(ops.createString("wait_time"), Codecs.INT.encode(ops, value.getWaitTime()));
        map.put(ops.createString("reapplication_delay"), Codecs.INT.encode(ops, value.getReapplicationDelay()));
        map.put(ops.createString("particle"), Codecs.STRING.encode(ops, value.getParticle().name()));
        map.put(ops.createString("color"), Codecs.COLOR.encode(ops, value.getColor()));

        if (value.getBasePotionType() != null) {
            map.put(ops.createString("base_potion_type"), Codecs.STRING.encode(ops, value.getBasePotionType().name()));
        }
        if (value.getSource() instanceof Entity sourceEntity) {
            map.put(ops.createString("source_uuid"), Codecs.UUID.encode(ops, sourceEntity.getUniqueId()));
        }
        if (value.getOwnerUniqueId() != null) {
            map.put(ops.createString("owner_uuid"), Codecs.UUID.encode(ops, value.getOwnerUniqueId()));
        }
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof AreaEffectCloud cloud)) return;

        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("duration")))).onSuccess(cloud::setDuration);
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("duration_on_use")))).onSuccess(cloud::setDurationOnUse);
        Try.of(() -> Codecs.FLOAT.decode(ops, map.get(ops.createString("radius")))).onSuccess(cloud::setRadius);
        Try.of(() -> Codecs.FLOAT.decode(ops, map.get(ops.createString("radius_on_use")))).onSuccess(cloud::setRadiusOnUse);
        Try.of(() -> Codecs.FLOAT.decode(ops, map.get(ops.createString("radius_per_tick")))).onSuccess(cloud::setRadiusPerTick);
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("wait_time")))).onSuccess(cloud::setWaitTime);
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("reapplication_delay")))).onSuccess(cloud::setReapplicationDelay);
        Try.of(() -> Codecs.STRING.decode(ops, map.get(ops.createString("particle")))).onSuccess(s -> cloud.setParticle(Particle.valueOf(s)));
        Try.of(() -> Codecs.COLOR.decode(ops, map.get(ops.createString("color")))).onSuccess(cloud::setColor);

        D potionTypeData = map.get(ops.createString("base_potion_type"));
        if (potionTypeData != null) {
            Try.of(() -> Codecs.STRING.decode(ops, potionTypeData)).onSuccess(s -> cloud.setBasePotionType(PotionType.valueOf(s)));
        }

        D sourceData = map.get(ops.createString("source_uuid"));
        if (sourceData != null) {
            Try.of(() -> Codecs.UUID.decode(ops, sourceData)).onSuccess(uuid -> {
                Entity source = Bukkit.getEntity(uuid);
                if (source instanceof org.bukkit.projectiles.ProjectileSource projSource) {
                    cloud.setSource(projSource);
                }
            });
        }

        D ownerData = map.get(ops.createString("owner_uuid"));
        if (ownerData != null) {
            Try.of(() -> Codecs.UUID.decode(ops, ownerData)).onSuccess(cloud::setOwnerUniqueId);
        }
    }
}