package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import org.bukkit.Sound;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;

import java.util.Map;

public class AbstractArrowEntityAdapter extends EntityAdapter<AbstractArrow> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof AbstractArrow;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, AbstractArrow value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("damage", Codecs.DOUBLE, value.getDamage())
            .write("piercing_level", Codecs.INT, value.getPierceLevel())
            .write("is_critical", Codecs.BOOLEAN, value.isCritical())
            .write("pickup_status", Codecs.STRING, value.getPickupStatus().name())
            .write("lifetime_ticks", Codecs.INT, value.getLifetimeTicks());

        Key sound = RegistryAccess.registryAccess().getRegistry(RegistryKey.SOUND_EVENT).getKey(value.getHitSound());
        if (sound != null) {
            ctx.write("hit_sound", Codecs.STRING, sound.asString());
        }

        ctx.write("item_stack", Codecs.ITEM_STACK, value.getItemStack());
        ctx.writeNullable("weapon", Codecs.ITEM_STACK, value.getWeapon());

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof AbstractArrow arrow)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("damage", Codecs.DOUBLE, opt -> opt.ifPresent(arrow::setDamage))
            .readOptional("piercing_level", Codecs.INT, opt -> opt.ifPresent(arrow::setPierceLevel))
            .readOptional("is_critical", Codecs.BOOLEAN, opt -> opt.ifPresent(arrow::setCritical))
            .readOptional("pickup_status", Codecs.STRING, opt -> opt.ifPresent(status -> {
                try {
                    arrow.setPickupStatus(AbstractArrow.PickupStatus.valueOf(status));
                } catch (Exception ignored) {
                }
            }))
            .readOptional("lifetime_ticks", Codecs.INT, opt -> opt.ifPresent(arrow::setLifetimeTicks))
            .readOptional("hit_sound", Codecs.STRING, opt -> opt.ifPresent(soundStr -> {
                try {
                    Sound sound = RegistryAccess.registryAccess().getRegistry(RegistryKey.SOUND_EVENT).get(Key.key(soundStr));
                    if (sound != null) arrow.setHitSound(sound);
                } catch (Exception ignored) {
                }
            }))
            .readOptional("item_stack", Codecs.ITEM_STACK, opt -> opt.ifPresent(arrow::setItemStack))
            .readOptional("weapon", Codecs.ITEM_STACK, opt -> opt.ifPresent(arrow::setWeapon));

        return ctx.result();
    }
}