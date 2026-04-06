package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import io.papermc.paper.entity.CollarColorable;
import org.bukkit.DyeColor;
import org.bukkit.entity.Entity;

import java.util.Map;

public class CollarColorableEntityAdapter extends EntityAdapter<CollarColorable> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof CollarColorable;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, CollarColorable value, Map<D, D> map) throws Codec.CodecException {
        if (value.getCollarColor() != null) {
            map.put(ops.createString("collar_color"), Codecs.STRING.encode(ops, value.getCollarColor().name()));
        }
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof CollarColorable colorable)) return;

        D colorData = map.get(ops.createString("collar_color"));
        if (colorData != null) {
            Try.of(() -> Codecs.STRING.decode(ops, colorData)).onSuccess(s -> colorable.setCollarColor(DyeColor.valueOf(s)));
        }
    }
}