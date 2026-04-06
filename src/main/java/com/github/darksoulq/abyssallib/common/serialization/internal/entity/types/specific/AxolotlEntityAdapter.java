package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.Entity;

import java.util.Map;

public class AxolotlEntityAdapter extends EntityAdapter<Axolotl> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Axolotl;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Axolotl value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("axolotl_variant"), Codecs.STRING.encode(ops, value.getVariant().name()));
        map.put(ops.createString("is_playing_dead"), Codecs.BOOLEAN.encode(ops, value.isPlayingDead()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Axolotl axolotl)) return;

        Try.of(() -> Codecs.STRING.decode(ops, map.get(ops.createString("axolotl_variant")))).onSuccess(s -> axolotl.setVariant(Axolotl.Variant.valueOf(s)));
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("is_playing_dead")))).onSuccess(axolotl::setPlayingDead);
    }
}