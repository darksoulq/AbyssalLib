package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Entity;
import org.bukkit.entity.minecart.CommandMinecart;

import java.util.Map;

public class CommandMinecartEntityAdapter extends EntityAdapter<CommandMinecart> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof CommandMinecart;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, CommandMinecart value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("command"), Codecs.STRING.encode(ops, value.getCommand()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof CommandMinecart minecart)) return;

        Try.of(() -> Codecs.STRING.decode(ops, map.get(ops.createString("command")))).onSuccess(minecart::setCommand);
    }
}