package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.DyeColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TropicalFish;

import java.util.Map;

public class TropicalFishEntityAdapter extends EntityAdapter<TropicalFish> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof TropicalFish;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, TropicalFish value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("pattern"), Codecs.STRING.encode(ops, value.getPattern().name()));
        map.put(ops.createString("body_color"), Codecs.STRING.encode(ops, value.getBodyColor().name()));
        map.put(ops.createString("pattern_color"), Codecs.STRING.encode(ops, value.getPatternColor().name()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof TropicalFish fish)) return;

        Try.of(() -> Codecs.STRING.decode(ops, map.get(ops.createString("pattern")))).onSuccess(s -> fish.setPattern(TropicalFish.Pattern.valueOf(s)));
        Try.of(() -> Codecs.STRING.decode(ops, map.get(ops.createString("body_color")))).onSuccess(s -> fish.setBodyColor(DyeColor.valueOf(s)));
        Try.of(() -> Codecs.STRING.decode(ops, map.get(ops.createString("pattern_color")))).onSuccess(s -> fish.setPatternColor(DyeColor.valueOf(s)));
    }
}