package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;

import java.util.Map;

public class ItemEntityAdapter extends EntityAdapter<Item> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Item;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Item value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("item_stack"), Codecs.ITEM_STACK.encode(ops, value.getItemStack()));
        map.put(ops.createString("pickup_delay"), Codecs.INT.encode(ops, value.getPickupDelay()));
        map.put(ops.createString("unlimited_lifetime"), Codecs.BOOLEAN.encode(ops, value.isUnlimitedLifetime()));
        map.put(ops.createString("can_mob_pickup"), Codecs.BOOLEAN.encode(ops, value.canMobPickup()));
        map.put(ops.createString("can_player_pickup"), Codecs.BOOLEAN.encode(ops, value.canPlayerPickup()));
        map.put(ops.createString("will_age"), Codecs.BOOLEAN.encode(ops, value.willAge()));
        map.put(ops.createString("health"), Codecs.INT.encode(ops, value.getHealth()));

        if (value.getOwner() != null) map.put(ops.createString("owner"), Codecs.UUID.encode(ops, value.getOwner()));
        if (value.getThrower() != null) map.put(ops.createString("thrower"), Codecs.UUID.encode(ops, value.getThrower()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Item item)) return;

        Try.of(() -> Codecs.ITEM_STACK.decode(ops, map.get(ops.createString("item_stack")))).onSuccess(item::setItemStack);
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("pickup_delay")))).onSuccess(item::setPickupDelay);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("unlimited_lifetime")))).onSuccess(item::setUnlimitedLifetime);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("can_mob_pickup")))).onSuccess(item::setCanMobPickup);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("can_player_pickup")))).onSuccess(item::setCanPlayerPickup);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("will_age")))).onSuccess(item::setWillAge);
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("health")))).onSuccess(item::setHealth);

        D ownerData = map.get(ops.createString("owner"));
        if (ownerData != null) Try.of(() -> Codecs.UUID.decode(ops, ownerData)).onSuccess(item::setOwner);

        D throwerData = map.get(ops.createString("thrower"));
        if (throwerData != null) Try.of(() -> Codecs.UUID.decode(ops, throwerData)).onSuccess(item::setThrower);
    }
}