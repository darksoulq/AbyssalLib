package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Turtle;

import java.util.Map;

public class TurtleEntityAdapter extends EntityAdapter<Turtle> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Turtle;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Turtle value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("has_egg"), Codecs.BOOLEAN.encode(ops, value.hasEgg()));
        map.put(ops.createString("home"), Codecs.LOCATION.encode(ops, value.getHome()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Turtle turtle)) return;

        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("has_egg")))).onSuccess(turtle::setHasEgg);
        Try.of(() -> Codecs.LOCATION.decode(ops, map.get(ops.createString("home")))).onSuccess(turtle::setHome);
    }
}