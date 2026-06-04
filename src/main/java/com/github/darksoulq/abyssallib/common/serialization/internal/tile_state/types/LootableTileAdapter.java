package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
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
    public <D> DataResult<D> serialize(DynamicOps<D> ops, Lootable value) {
        if (!value.hasLootTable() && value.getSeed() == 0) {
            return DataResult.error(DataError.custom("No loot table or seed"));
        }

        Map<D, D> map = new HashMap<>();

        if (value.getLootTable() != null) {
            DataResult<D> lootRes = Codecs.NAMESPACED_KEY.encode(ops, value.getLootTable().getKey()).prependPath("loot_table");
            if (lootRes.isError()) return DataResult.error(lootRes.error().get());
            map.put(ops.createString("loot_table"), lootRes.getOrThrow());
            if (lootRes.isPartial()) return DataResult.partial(ops.createMap(map), lootRes.warnings());
        }

        DataResult<D> seedRes = Codecs.LONG.encode(ops, value.getSeed()).prependPath("seed");
        if (seedRes.isError()) return DataResult.error(seedRes.error().get());
        map.put(ops.createString("seed"), seedRes.getOrThrow());

        return seedRes.isPartial() ? DataResult.partial(ops.createMap(map), seedRes.warnings()) : DataResult.success(ops.createMap(map));
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, TileState base) {
        if (!(base instanceof Lootable lootable)) return DataResult.success(null);

        return ops.getMap(input)
            .map(DataResult::success)
            .orElseGet(() -> DataResult.error(DataError.typeMismatch("Map", "Unknown")))
            .flatMap(map -> {
                D tableData = map.get(ops.createString("loot_table"));
                if (tableData != null) {
                    try {
                        org.bukkit.NamespacedKey key = Codecs.NAMESPACED_KEY.decode(ops, tableData).getOrThrow();
                        LootTable table = Bukkit.getLootTable(key);
                        if (table != null) lootable.setLootTable(table);
                    } catch (Exception ignored) {
                    }
                }

                D seedData = map.get(ops.createString("seed"));
                if (seedData != null) {
                    try {
                        lootable.setSeed(Codecs.LONG.decode(ops, seedData).getOrThrow());
                    } catch (Exception ignored) {
                    }
                }

                return DataResult.success(null);
            });
    }
}