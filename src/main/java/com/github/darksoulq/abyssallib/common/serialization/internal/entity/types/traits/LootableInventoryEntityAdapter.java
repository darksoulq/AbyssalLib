package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.destroystokyo.paper.loottable.LootableInventory;
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Entity;

import java.util.Map;

public class LootableInventoryEntityAdapter extends EntityAdapter<LootableInventory> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof LootableInventory;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, LootableInventory value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("next_refill"), Codecs.LONG.encode(ops, value.getNextRefill()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof LootableInventory lootable)) return;

        Try.of(() -> Codecs.LONG.decode(ops, map.get(ops.createString("next_refill")))).onSuccess(lootable::setNextRefill);
    }
}