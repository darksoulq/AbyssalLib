package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.TileAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import io.papermc.paper.block.TileStateInventoryHolder;
import org.bukkit.block.TileState;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TileStateInventoryHolderAdapter extends TileAdapter<TileStateInventoryHolder> {

    @Override
    public boolean doesApply(TileState state) {
        return state instanceof TileStateInventoryHolder;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, TileStateInventoryHolder value) throws Codec.CodecException {
        Inventory inventory = value.getInventory();
        List<D> items = new ArrayList<>();

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null && !item.isEmpty()) {
                items.add(Codecs.ITEM_STACK.encode(ops, item));
            } else {
                items.add(ops.createString("empty"));
            }
        }

        return ops.createList(items);
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, TileState base) throws Codec.CodecException {
        if (!(base instanceof TileStateInventoryHolder holder)) return;
        Inventory inventory = holder.getInventory();

        ops.getList(input).ifPresent(list -> {
            for (int i = 0; i < inventory.getSize() && i < list.size(); i++) {
                D itemData = list.get(i);

                if (ops.getStringValue(itemData).orElse("").equals("empty")) {
                    inventory.setItem(i, null);
                } else {
                    int finalI = i;
                    Try.of(() -> Codecs.ITEM_STACK.decode(ops, itemData))
                       .onSuccess(item -> inventory.setItem(finalI, item));
                }
            }
        });
    }
}