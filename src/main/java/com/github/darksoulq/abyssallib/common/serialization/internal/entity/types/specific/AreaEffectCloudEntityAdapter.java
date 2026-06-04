package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
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
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, AreaEffectCloud value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("duration", Codecs.INT, value.getDuration())
            .write("duration_on_use", Codecs.INT, value.getDurationOnUse())
            .write("radius", Codecs.FLOAT, value.getRadius())
            .write("radius_on_use", Codecs.FLOAT, value.getRadiusOnUse())
            .write("radius_per_tick", Codecs.FLOAT, value.getRadiusPerTick())
            .write("wait_time", Codecs.INT, value.getWaitTime())
            .write("reapplication_delay", Codecs.INT, value.getReapplicationDelay())
            .write("particle", Codecs.STRING, value.getParticle().name())
            .write("color", Codecs.COLOR, value.getColor());

        if (value.getBasePotionType() != null) {
            ctx.write("base_potion_type", Codecs.STRING, value.getBasePotionType().name());
        }

        if (value.getSource() instanceof Entity sourceEntity) {
            ctx.write("source_uuid", Codecs.UUID, sourceEntity.getUniqueId());
        }

        ctx.writeNullable("owner_uuid", Codecs.UUID, value.getOwnerUniqueId());

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof AreaEffectCloud cloud)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("duration", Codecs.INT, opt -> opt.ifPresent(cloud::setDuration))
            .readOptional("duration_on_use", Codecs.INT, opt -> opt.ifPresent(cloud::setDurationOnUse))
            .readOptional("radius", Codecs.FLOAT, opt -> opt.ifPresent(cloud::setRadius))
            .readOptional("radius_on_use", Codecs.FLOAT, opt -> opt.ifPresent(cloud::setRadiusOnUse))
            .readOptional("radius_per_tick", Codecs.FLOAT, opt -> opt.ifPresent(cloud::setRadiusPerTick))
            .readOptional("wait_time", Codecs.INT, opt -> opt.ifPresent(cloud::setWaitTime))
            .readOptional("reapplication_delay", Codecs.INT, opt -> opt.ifPresent(cloud::setReapplicationDelay))
            .readOptional("particle", Codecs.STRING, opt -> opt.ifPresent(particleStr -> {
                try {
                    cloud.setParticle(Particle.valueOf(particleStr));
                } catch (Exception ignored) {
                }
            }))
            .readOptional("color", Codecs.COLOR, opt -> opt.ifPresent(cloud::setColor))
            .readOptional("base_potion_type", Codecs.STRING, opt -> opt.ifPresent(potionStr -> {
                try {
                    cloud.setBasePotionType(PotionType.valueOf(potionStr));
                } catch (Exception ignored) {
                }
            }))
            .readOptional("source_uuid", Codecs.UUID, opt -> opt.ifPresent(uuid -> {
                Entity source = Bukkit.getEntity(uuid);
                if (source instanceof org.bukkit.projectiles.ProjectileSource projSource) {
                    cloud.setSource(projSource);
                }
            }))
            .readOptional("owner_uuid", Codecs.UUID, opt -> opt.ifPresent(cloud::setOwnerUniqueId));

        return ctx.result();
    }
}