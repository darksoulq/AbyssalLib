package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Entity;
import org.bukkit.entity.minecart.HopperMinecart;

import java.util.Map;

public class HopperMinecartEntityAdapter extends EntityAdapter<HopperMinecart> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof HopperMinecart;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, HopperMinecart value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("is_enabled"), Codecs.BOOLEAN.encode(ops, value.isEnabled()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof HopperMinecart minecart)) return;

        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("is_enabled")))).onSuccess(minecart::setEnabled);
    }
}