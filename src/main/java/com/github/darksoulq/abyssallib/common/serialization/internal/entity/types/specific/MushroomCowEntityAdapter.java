package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Entity;
import org.bukkit.entity.MushroomCow;

import java.util.Map;

public class MushroomCowEntityAdapter extends EntityAdapter<MushroomCow> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof MushroomCow;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, MushroomCow value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("variant"), Codecs.STRING.encode(ops, value.getVariant().name()));

        if (!value.getStewEffects().isEmpty()) {
            map.put(ops.createString("stew_effects"), ExtraCodecs.SUSPICIOUS_EFFECT_ENTRY.list().encode(ops, value.getStewEffects()));
        }
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof MushroomCow mooshroom)) return;

        Try.of(() -> Codecs.STRING.decode(ops, map.get(ops.createString("variant")))).onSuccess(s -> mooshroom.setVariant(MushroomCow.Variant.valueOf(s)));

        D effectsData = map.get(ops.createString("stew_effects"));
        if (effectsData != null) {
            Try.of(() -> ExtraCodecs.SUSPICIOUS_EFFECT_ENTRY.list().decode(ops, effectsData)).onSuccess(mooshroom::setStewEffects);
        }
    }
}