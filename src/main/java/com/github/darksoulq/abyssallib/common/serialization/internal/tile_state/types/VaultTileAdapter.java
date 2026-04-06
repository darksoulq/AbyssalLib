package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.TileAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.block.TileState;
import org.bukkit.block.Vault;
import org.bukkit.loot.LootTable;

import java.util.HashMap;
import java.util.Map;

public class VaultTileAdapter extends TileAdapter<Vault> {

    @Override
    public boolean doesApply(TileState state) {
        return state instanceof Vault;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, Vault value) throws Codec.CodecException {
        Map<D, D> map = new HashMap<>();

        map.put(ops.createString("activation_range"), Codecs.DOUBLE.encode(ops, value.getActivationRange()));
        map.put(ops.createString("deactivation_range"), Codecs.DOUBLE.encode(ops, value.getDeactivationRange()));
        map.put(ops.createString("next_update"), Codecs.LONG.encode(ops, value.getNextStateUpdateTime()));

        if (!value.getKeyItem().isEmpty()) {
            map.put(ops.createString("key_item"), Codecs.ITEM_STACK.encode(ops, value.getKeyItem()));
        }

        if (!value.getDisplayedItem().isEmpty()) {
            map.put(ops.createString("displayed_item"), Codecs.ITEM_STACK.encode(ops, value.getDisplayedItem()));
        }

        map.put(ops.createString("loot_table"), Codecs.NAMESPACED_KEY.encode(ops, value.getLootTable().getKey()));

        if (value.getDisplayedLootTable() != null) {
            map.put(ops.createString("displayed_loot_table"), Codecs.NAMESPACED_KEY.encode(ops, value.getDisplayedLootTable().getKey()));
        }

        return ops.createMap(map);
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, TileState base) throws Codec.CodecException {
        if (!(base instanceof Vault vault)) return;
        Map<D, D> map = ops.getMap(input).orElseThrow(() -> new Codec.CodecException("Expected map for Vault"));

        Try.of(() -> Codecs.DOUBLE.decode(ops, map.get(ops.createString("activation_range")))).onSuccess(vault::setActivationRange);
        Try.of(() -> Codecs.DOUBLE.decode(ops, map.get(ops.createString("deactivation_range")))).onSuccess(vault::setDeactivationRange);
        Try.of(() -> Codecs.LONG.decode(ops, map.get(ops.createString("next_update")))).onSuccess(vault::setNextStateUpdateTime);

        D keyData = map.get(ops.createString("key_item"));
        if (keyData != null) {
            Try.of(() -> Codecs.ITEM_STACK.decode(ops, keyData)).onSuccess(vault::setKeyItem);
        }

        D dispItemData = map.get(ops.createString("displayed_item"));
        if (dispItemData != null) {
            Try.of(() -> Codecs.ITEM_STACK.decode(ops, dispItemData)).onSuccess(vault::setDisplayedItem);
        }

        D tableData = map.get(ops.createString("loot_table"));
        if (tableData != null) {
            Try.of(() -> Codecs.NAMESPACED_KEY.decode(ops, tableData)).onSuccess(k -> {
                LootTable table = org.bukkit.Bukkit.getLootTable(k);
                if (table != null) vault.setLootTable(table);
            });
        }

        D dispTableData = map.get(ops.createString("displayed_loot_table"));
        if (dispTableData != null) {
            Try.of(() -> Codecs.NAMESPACED_KEY.decode(ops, dispTableData)).onSuccess(k -> {
                org.bukkit.loot.LootTable table = org.bukkit.Bukkit.getLootTable(k);
                if (table != null) vault.setDisplayedLootTable(table);
            });
        }
    }
}