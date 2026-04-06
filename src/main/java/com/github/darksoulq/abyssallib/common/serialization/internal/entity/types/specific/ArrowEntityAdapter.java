package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.potion.PotionType;

import java.util.Map;

public class ArrowEntityAdapter extends EntityAdapter<Arrow> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Arrow;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Arrow value, Map<D, D> map) throws Codec.CodecException {
        if (value.getBasePotionType() != null) {
            map.put(ops.createString("base_potion_type"), Codecs.STRING.encode(ops, value.getBasePotionType().name()));
        }
        if (value.getColor() != null) {
            map.put(ops.createString("color"), Codecs.COLOR.encode(ops, value.getColor()));
        }
        if (value.hasCustomEffects()) {
            map.put(ops.createString("custom_effects"), ExtraCodecs.POTION_EFFECT.list().encode(ops, value.getCustomEffects()));
        }
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Arrow arrow)) return;

        D typeData = map.get(ops.createString("base_potion_type"));
        if (typeData != null) {
            Try.of(() -> Codecs.STRING.decode(ops, typeData)).onSuccess(s -> arrow.setBasePotionType(PotionType.valueOf(s)));
        }

        D colorData = map.get(ops.createString("color"));
        if (colorData != null) {
            Try.of(() -> Codecs.COLOR.decode(ops, colorData)).onSuccess(arrow::setColor);
        }

        D effectsData = map.get(ops.createString("custom_effects"));
        if (effectsData != null) {
            Try.of(() -> ExtraCodecs.POTION_EFFECT.list().decode(ops, effectsData)).onSuccess(list -> {
                list.forEach(effect -> arrow.addCustomEffect(effect, true));
            });
        }
    }
}