package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.TileAdapter;
import org.bukkit.Bukkit;
import org.bukkit.block.TileState;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.Lootable;

import java.util.HashMap;
import java.util.Map;

public class LootableTileAdapter extends TileAdapter<Lootable> {

    @Override
    public boolean doesApply(TileState state) {
        return state instanceof Lootable;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, Lootable value) throws Codec.CodecException {
        if (!value.hasLootTable() && value.getSeed() == 0) {
            throw new Codec.CodecException("No loot table or seed");
        }

        Map<D, D> map = new HashMap<>();
        if (value.getLootTable() != null) {
            map.put(ops.createString("loot_table"), Codecs.NAMESPACED_KEY.encode(ops, value.getLootTable().getKey()));
        }
        map.put(ops.createString("seed"), Codecs.LONG.encode(ops, value.getSeed()));
        return ops.createMap(map);
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, TileState base) throws Codec.CodecException {
        if (!(base instanceof Lootable lootable)) return;

        Map<D, D> map = ops.getMap(input).orElseThrow(() -> new Codec.CodecException("Expected map for Lootable"));

        D tableData = map.get(ops.createString("loot_table"));
        if (tableData != null) {
            try {
                LootTable table = Bukkit.getLootTable(Codecs.NAMESPACED_KEY.decode(ops, tableData));
                if (table != null) lootable.setLootTable(table);
            } catch (Exception ignored) {}
        }

        D seedData = map.get(ops.createString("seed"));
        if (seedData != null) {
            try {
                lootable.setSeed(Codecs.LONG.decode(ops, seedData));
            } catch (Exception ignored) {}
        }
    }
}