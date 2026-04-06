package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.DyeColor;
import org.bukkit.material.Colorable;
import org.bukkit.entity.Entity;

import java.util.Map;

public class ColorableEntityAdapter extends EntityAdapter<Colorable> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Colorable;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Colorable value, Map<D, D> map) throws Codec.CodecException {
        if (value.getColor() != null) {
            map.put(ops.createString("color"), Codecs.STRING.encode(ops, value.getColor().name()));
        }
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Colorable colorable)) return;
        D colorData = map.get(ops.createString("color"));
        if (colorData != null) {
            Try.of(() -> Codecs.STRING.decode(ops, colorData)).onSuccess(c -> colorable.setColor(DyeColor.valueOf(c)));
        }
    }
}