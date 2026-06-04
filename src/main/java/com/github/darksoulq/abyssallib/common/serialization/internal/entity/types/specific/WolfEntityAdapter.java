package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
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
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Wolf value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("is_angry", Codecs.BOOLEAN, value.isAngry())
            .write("is_interested", Codecs.BOOLEAN, value.isInterested())
            .write("wolf_variant", Codecs.NAMESPACED_KEY, value.getVariant().getKey())
            .write("wolf_sound_variant", Codecs.NAMESPACED_KEY, value.getSoundVariant().getKey());

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Wolf wolf)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("is_angry", Codecs.BOOLEAN, opt -> opt.ifPresent(wolf::setAngry))
            .readOptional("is_interested", Codecs.BOOLEAN, opt -> opt.ifPresent(wolf::setInterested))
            .readOptional("wolf_variant", Codecs.NAMESPACED_KEY, opt -> opt.ifPresent(key -> {
                Wolf.Variant variant = RegistryAccess.registryAccess().getRegistry(RegistryKey.WOLF_VARIANT).get(key);
                if (variant != null) wolf.setVariant(variant);
            }))
            .readOptional("wolf_sound_variant", Codecs.NAMESPACED_KEY, opt -> opt.ifPresent(key -> {
                Wolf.SoundVariant soundVariant = RegistryAccess.registryAccess().getRegistry(RegistryKey.WOLF_SOUND_VARIANT).get(key);
                if (soundVariant != null) wolf.setSoundVariant(soundVariant);
            }));

        return ctx.result();
    }
}