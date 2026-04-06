package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Entity;
import org.bukkit.entity.WanderingTrader;

import java.util.Map;

public class WanderingTraderEntityAdapter extends EntityAdapter<WanderingTrader> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof WanderingTrader;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, WanderingTrader value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("despawn_delay"), Codecs.INT.encode(ops, value.getDespawnDelay()));
        map.put(ops.createString("can_drink_potion"), Codecs.BOOLEAN.encode(ops, value.canDrinkPotion()));
        map.put(ops.createString("can_drink_milk"), Codecs.BOOLEAN.encode(ops, value.canDrinkMilk()));

        if (value.getWanderingTowards() != null) {
            map.put(ops.createString("wandering_towards"), Codecs.LOCATION.encode(ops, value.getWanderingTowards()));
        }
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof WanderingTrader trader)) return;

        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("despawn_delay")))).onSuccess(trader::setDespawnDelay);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("can_drink_potion")))).onSuccess(trader::setCanDrinkPotion);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("can_drink_milk")))).onSuccess(trader::setCanDrinkMilk);

        D locData = map.get(ops.createString("wandering_towards"));
        if (locData != null) {
            Try.of(() -> Codecs.LOCATION.decode(ops, locData)).onSuccess(trader::setWanderingTowards);
        }
    }
}