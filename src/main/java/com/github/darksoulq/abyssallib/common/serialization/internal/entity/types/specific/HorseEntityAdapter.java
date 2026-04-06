package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;

import java.util.Map;

public class HorseEntityAdapter extends EntityAdapter<Horse> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Horse;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Horse value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("horse_color"), Codecs.STRING.encode(ops, value.getColor().name()));
        map.put(ops.createString("horse_style"), Codecs.STRING.encode(ops, value.getStyle().name()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Horse horse)) return;

        Try.of(() -> Codecs.STRING.decode(ops, map.get(ops.createString("horse_color")))).onSuccess(s -> horse.setColor(Horse.Color.valueOf(s)));
        Try.of(() -> Codecs.STRING.decode(ops, map.get(ops.createString("horse_style")))).onSuccess(s -> horse.setStyle(Horse.Style.valueOf(s)));
    }
}