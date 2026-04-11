package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Entity;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.Lootable;

import java.util.Map;

public class LootableEntityAdapter extends EntityAdapter<Lootable> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Lootable;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Lootable value, Map<D, D> map) throws Codec.CodecException {
        if (value.getLootTable() != null) {
            map.put(ops.createString("loot_table"), Codecs.NAMESPACED_KEY.encode(ops, value.getLootTable().getKey()));
        }
        map.put(ops.createString("seed"), Codecs.LONG.encode(ops, value.getSeed()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Lootable lootable)) return;

        D tableData = map.get(ops.createString("loot_table"));
        if (tableData != null) {
            Try.of(() -> Codecs.NAMESPACED_KEY.decode(ops, tableData)).onSuccess(key -> {
                LootTable table = org.bukkit.Bukkit.getLootTable(key);
                if (table != null) lootable.setLootTable(table);
            });
        }
        
        Try.of(() -> Codecs.LONG.decode(ops, map.get(ops.createString("seed")))).onSuccess(lootable::setSeed);
    }
}