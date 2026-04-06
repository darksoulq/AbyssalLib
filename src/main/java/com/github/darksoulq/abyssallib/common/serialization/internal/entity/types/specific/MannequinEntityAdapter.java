package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mannequin;
import org.bukkit.inventory.MainHand;

import java.util.Map;

public class MannequinEntityAdapter extends EntityAdapter<Mannequin> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Mannequin;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Mannequin value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("is_immovable"), Codecs.BOOLEAN.encode(ops, value.isImmovable()));
        map.put(ops.createString("main_hand"), Codecs.STRING.encode(ops, value.getMainHand().name()));
        map.put(ops.createString("profile"), ExtraCodecs.RESOLVABLE_PROFILE.encode(ops, value.getProfile()));
        
        if (value.getDescription() != null) {
            map.put(ops.createString("description"), Codecs.TEXT_COMPONENT.encode(ops, value.getDescription()));
        }
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Mannequin mannequin)) return;

        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("is_immovable")))).onSuccess(mannequin::setImmovable);
        Try.of(() -> Codecs.STRING.decode(ops, map.get(ops.createString("main_hand")))).onSuccess(s -> mannequin.setMainHand(MainHand.valueOf(s)));
        Try.of(() -> ExtraCodecs.RESOLVABLE_PROFILE.decode(ops, map.get(ops.createString("profile")))).onSuccess(mannequin::setProfile);

        D descData = map.get(ops.createString("description"));
        if (descData != null) {
            Try.of(() -> Codecs.TEXT_COMPONENT.decode(ops, descData)).onSuccess(mannequin::setDescription);
        }
    }
}