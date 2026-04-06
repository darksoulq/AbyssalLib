package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Wolf;

import java.util.Map;

public class WolfEntityAdapter extends EntityAdapter<Wolf> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Wolf;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Wolf value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("is_angry"), Codecs.BOOLEAN.encode(ops, value.isAngry()));
        map.put(ops.createString("is_interested"), Codecs.BOOLEAN.encode(ops, value.isInterested()));
        map.put(ops.createString("wolf_variant"), Codecs.NAMESPACED_KEY.encode(ops, value.getVariant().getKey()));
        map.put(ops.createString("wolf_sound_variant"), Codecs.NAMESPACED_KEY.encode(ops, value.getSoundVariant().getKey()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Wolf wolf)) return;

        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("is_angry")))).onSuccess(wolf::setAngry);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("is_interested")))).onSuccess(wolf::setInterested);

        D variantData = map.get(ops.createString("wolf_variant"));
        Try.of(() -> Codecs.NAMESPACED_KEY.decode(ops, variantData)).onSuccess(key -> {
            Wolf.Variant variant = RegistryAccess.registryAccess().getRegistry(RegistryKey.WOLF_VARIANT).get(key);
            if (variant != null) wolf.setVariant(variant);
        });

        D soundVariantData = map.get(ops.createString("wolf_sound_variant"));
        Try.of(() -> Codecs.NAMESPACED_KEY.decode(ops, soundVariantData)).onSuccess(key -> {
            Wolf.SoundVariant soundVariant = RegistryAccess.registryAccess().getRegistry(RegistryKey.WOLF_SOUND_VARIANT).get(key);
            if (soundVariant != null) wolf.setSoundVariant(soundVariant);
        });
    }
}