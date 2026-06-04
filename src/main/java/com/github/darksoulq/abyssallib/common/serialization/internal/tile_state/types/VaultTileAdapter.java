package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.TileAdapter;
import org.bukkit.block.TileState;
import org.bukkit.block.Vault;
import org.bukkit.loot.LootTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VaultTileAdapter extends TileAdapter<Vault> {

    @Override
    public boolean doesApply(TileState state) {
        return state instanceof Vault;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, Vault value) {
        Map<D, D> map = new HashMap<>();
        List<DataError> warnings = new ArrayList<>();

        DataResult<D> actRes = Codecs.DOUBLE.encode(ops, value.getActivationRange()).prependPath("activation_range");
        if (actRes.isError()) warnings.add(actRes.dataError().orElseGet(() -> DataError.custom(actRes.error().get())));
        else map.put(ops.createString("activation_range"), actRes.getOrThrow());

        DataResult<D> deactRes = Codecs.DOUBLE.encode(ops, value.getDeactivationRange()).prependPath("deactivation_range");
        if (deactRes.isError())
            warnings.add(deactRes.dataError().orElseGet(() -> DataError.custom(deactRes.error().get())));
        else map.put(ops.createString("deactivation_range"), deactRes.getOrThrow());

        DataResult<D> nextRes = Codecs.LONG.encode(ops, value.getNextStateUpdateTime()).prependPath("next_update");
        if (nextRes.isError())
            warnings.add(nextRes.dataError().orElseGet(() -> DataError.custom(nextRes.error().get())));
        else map.put(ops.createString("next_update"), nextRes.getOrThrow());

        if (!value.getKeyItem().isEmpty()) {
            DataResult<D> keyRes = Codecs.ITEM_STACK.encode(ops, value.getKeyItem()).prependPath("key_item");
            if (keyRes.isError())
                warnings.add(keyRes.dataError().orElseGet(() -> DataError.custom(keyRes.error().get())));
            else map.put(ops.createString("key_item"), keyRes.getOrThrow());
        }

        if (!value.getDisplayedItem().isEmpty()) {
            DataResult<D> dispRes = Codecs.ITEM_STACK.encode(ops, value.getDisplayedItem()).prependPath("displayed_item");
            if (dispRes.isError())
                warnings.add(dispRes.dataError().orElseGet(() -> DataError.custom(dispRes.error().get())));
            else map.put(ops.createString("displayed_item"), dispRes.getOrThrow());
        }

        DataResult<D> lootRes = Codecs.NAMESPACED_KEY.encode(ops, value.getLootTable().getKey()).prependPath("loot_table");
        if (lootRes.isError())
            warnings.add(lootRes.dataError().orElseGet(() -> DataError.custom(lootRes.error().get())));
        else map.put(ops.createString("loot_table"), lootRes.getOrThrow());

        if (value.getDisplayedLootTable() != null) {
            DataResult<D> dispLootRes = Codecs.NAMESPACED_KEY.encode(ops, value.getDisplayedLootTable().getKey()).prependPath("displayed_loot_table");
            if (dispLootRes.isError())
                warnings.add(dispLootRes.dataError().orElseGet(() -> DataError.custom(dispLootRes.error().get())));
            else map.put(ops.createString("displayed_loot_table"), dispLootRes.getOrThrow());
        }

        return warnings.isEmpty() ? DataResult.success(ops.createMap(map)) : DataResult.partial(ops.createMap(map), warnings);
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, TileState base) {
        if (!(base instanceof Vault vault)) return DataResult.success(null);

        return ops.getMap(input)
            .map(DataResult::success)
            .orElseGet(() -> DataResult.error(DataError.typeMismatch("Map", "Unknown")))
            .flatMap(map -> {
                List<DataError> warnings = new ArrayList<>();

                D actData = map.get(ops.createString("activation_range"));
                if (actData != null) {
                    DataResult<Double> res = Codecs.DOUBLE.decode(ops, actData).prependPath("activation_range");
                    if (res.isError())
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    else vault.setActivationRange(res.getOrThrow());
                }

                D deactData = map.get(ops.createString("deactivation_range"));
                if (deactData != null) {
                    DataResult<Double> res = Codecs.DOUBLE.decode(ops, deactData).prependPath("deactivation_range");
                    if (res.isError())
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    else vault.setDeactivationRange(res.getOrThrow());
                }

                D nextData = map.get(ops.createString("next_update"));
                if (nextData != null) {
                    DataResult<Long> res = Codecs.LONG.decode(ops, nextData).prependPath("next_update");
                    if (res.isError())
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    else vault.setNextStateUpdateTime(res.getOrThrow());
                }

                D keyData = map.get(ops.createString("key_item"));
                if (keyData != null) {
                    DataResult<org.bukkit.inventory.ItemStack> res = Codecs.ITEM_STACK.decode(ops, keyData).prependPath("key_item");
                    if (res.isError())
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    else vault.setKeyItem(res.getOrThrow());
                }

                D dispData = map.get(ops.createString("displayed_item"));
                if (dispData != null) {
                    DataResult<org.bukkit.inventory.ItemStack> res = Codecs.ITEM_STACK.decode(ops, dispData).prependPath("displayed_item");
                    if (res.isError())
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    else vault.setDisplayedItem(res.getOrThrow());
                }

                D tableData = map.get(ops.createString("loot_table"));
                if (tableData != null) {
                    DataResult<org.bukkit.NamespacedKey> res = Codecs.NAMESPACED_KEY.decode(ops, tableData).prependPath("loot_table");
                    if (res.isError())
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    else {
                        LootTable table = org.bukkit.Bukkit.getLootTable(res.getOrThrow());
                        if (table != null) vault.setLootTable(table);
                    }
                }

                D dispTableData = map.get(ops.createString("displayed_loot_table"));
                if (dispTableData != null) {
                    DataResult<org.bukkit.NamespacedKey> res = Codecs.NAMESPACED_KEY.decode(ops, dispTableData).prependPath("displayed_loot_table");
                    if (res.isError())
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    else {
                        LootTable table = org.bukkit.Bukkit.getLootTable(res.getOrThrow());
                        if (table != null) vault.setDisplayedLootTable(table);
                    }
                }

                return warnings.isEmpty() ? DataResult.success(null) : DataResult.partial(null, warnings);
            });
    }
}