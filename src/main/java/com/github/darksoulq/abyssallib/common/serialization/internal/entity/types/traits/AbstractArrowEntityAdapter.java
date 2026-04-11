package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
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
    public <D> void serialize(DynamicOps<D> ops, AbstractArrow value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("damage"), Codecs.DOUBLE.encode(ops, value.getDamage()));
        map.put(ops.createString("piercing_level"), Codecs.INT.encode(ops, value.getPierceLevel()));
        map.put(ops.createString("is_critical"), Codecs.BOOLEAN.encode(ops, value.isCritical()));
        map.put(ops.createString("pickup_status"), Codecs.STRING.encode(ops, value.getPickupStatus().name()));
        map.put(ops.createString("lifetime_ticks"), Codecs.INT.encode(ops, value.getLifetimeTicks()));
        Key sound = RegistryAccess.registryAccess().getRegistry(RegistryKey.SOUND_EVENT).getKey(value.getHitSound());
        if (sound != null) map.put(ops.createString("hit_sound"), Codecs.STRING.encode(ops, sound.asString()));
        map.put(ops.createString("item_stack"), Codecs.ITEM_STACK.encode(ops, value.getItemStack()));
        if (value.getWeapon() != null) {
            map.put(ops.createString("weapon"), Codecs.ITEM_STACK.encode(ops, value.getWeapon()));
        }
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof AbstractArrow arrow)) return;

        Try.of(() -> Codecs.DOUBLE.decode(ops, map.get(ops.createString("damage")))).onSuccess(arrow::setDamage);
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("piercing_level")))).onSuccess(arrow::setPierceLevel);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("is_critical")))).onSuccess(arrow::setCritical);
        Try.of(() -> Codecs.STRING.decode(ops, map.get(ops.createString("pickup_status")))).onSuccess(s -> arrow.setPickupStatus(AbstractArrow.PickupStatus.valueOf(s)));
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("lifetime_ticks")))).onSuccess(arrow::setLifetimeTicks);

        D soundData = map.get(ops.createString("hit_sound"));
        if (soundData != null) {
            Try.of(() -> Codecs.STRING.decode(ops, soundData)).onSuccess(keyString -> {
                Sound sound = RegistryAccess.registryAccess().getRegistry(RegistryKey.SOUND_EVENT).get(Key.key(keyString));
                if (sound != null) arrow.setHitSound(sound);
            });
        }
        Try.of(() -> Codecs.ITEM_STACK.decode(ops, map.get(ops.createString("item_stack")))).onSuccess(arrow::setItemStack);

        D weaponData = map.get(ops.createString("weapon"));
        if (weaponData != null) {
            Try.of(() -> Codecs.ITEM_STACK.decode(ops, weaponData)).onSuccess(arrow::setWeapon);
        }
    }
}