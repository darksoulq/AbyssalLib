package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Vex;

import java.util.Map;

public class VexEntityAdapter extends EntityAdapter<Vex> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Vex;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Vex value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("is_charging"), Codecs.BOOLEAN.encode(ops, value.isCharging()));
        map.put(ops.createString("has_limited_lifetime"), Codecs.BOOLEAN.encode(ops, value.hasLimitedLifetime()));
        map.put(ops.createString("limited_lifetime_ticks"), Codecs.INT.encode(ops, value.getLimitedLifetimeTicks()));

        if (value.getBound() != null) {
            map.put(ops.createString("bound_location"), Codecs.LOCATION.encode(ops, value.getBound()));
        }
        if (value.getSummoner() != null) {
            map.put(ops.createString("summoner_uuid"), Codecs.UUID.encode(ops, value.getSummoner().getUniqueId()));
        }
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Vex vex)) return;

        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("is_charging")))).onSuccess(vex::setCharging);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("has_limited_lifetime")))).onSuccess(vex::setLimitedLifetime);
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("limited_lifetime_ticks")))).onSuccess(vex::setLimitedLifetimeTicks);

        D boundData = map.get(ops.createString("bound_location"));
        if (boundData != null) {
            Try.of(() -> Codecs.LOCATION.decode(ops, boundData)).onSuccess(vex::setBound);
        }

        D summonerData = map.get(ops.createString("summoner_uuid"));
        if (summonerData != null) {
            Try.of(() -> Codecs.UUID.decode(ops, summonerData)).onSuccess(uuid -> {
                Entity summoner = Bukkit.getEntity(uuid);
                if (summoner instanceof Mob mob) vex.setSummoner(mob);
            });
        }
    }
}