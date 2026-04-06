package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Trident;

import java.util.Map;

public class TridentEntityAdapter extends EntityAdapter<Trident> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Trident;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Trident value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("has_glint"), Codecs.BOOLEAN.encode(ops, value.hasGlint()));
        map.put(ops.createString("loyalty_level"), Codecs.INT.encode(ops, value.getLoyaltyLevel()));
        map.put(ops.createString("has_dealt_damage"), Codecs.BOOLEAN.encode(ops, value.hasDealtDamage()));
        map.put(ops.createString("damage"), Codecs.DOUBLE.encode(ops, value.getDamage()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Trident trident)) return;

        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("has_glint")))).onSuccess(trident::setGlint);
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("loyalty_level")))).onSuccess(trident::setLoyaltyLevel);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("has_dealt_damage")))).onSuccess(trident::setHasDealtDamage);
        Try.of(() -> Codecs.DOUBLE.decode(ops, map.get(ops.createString("damage")))).onSuccess(trident::setDamage);
    }
}