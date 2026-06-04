package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.TileAdapter;
import io.papermc.paper.block.TileStateInventoryHolder;
import org.bukkit.block.TileState;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.Lootable;

import java.util.ArrayList;
import java.util.List;

public class TileStateInventoryHolderAdapter extends TileAdapter<TileStateInventoryHolder> {

    @Override
    public boolean doesApply(TileState state) {
        return state instanceof TileStateInventoryHolder;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, TileStateInventoryHolder value) {
        List<D> items = new ArrayList<>();
        List<DataError> warnings = new ArrayList<>();

        if (value instanceof Lootable lootable && lootable.hasLootTable()) {
            return DataResult.success(ops.createList(items));
        }

        Inventory inventory = value.getInventory();
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null && !item.isEmpty()) {
                DataResult<D> res = Codecs.ITEM_STACK.encode(ops, item).prependPath("[" + i + "]");
                if (res.isError()) {
                    warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    items.add(ops.createString("empty"));
                } else {
                    items.add(res.getOrThrow());
                    if (res.isPartial()) warnings.addAll(res.warnings());
                }
            } else {
                items.add(ops.createString("empty"));
            }
        }

        return warnings.isEmpty() ? DataResult.success(ops.createList(items)) : DataResult.partial(ops.createList(items), warnings);
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, TileState base) {
        if (!(base instanceof TileStateInventoryHolder holder)) return DataResult.success(null);

        return ops.getList(input)
            .map(DataResult::success)
            .orElseGet(() -> DataResult.error(DataError.typeMismatch("List", "Unknown")))
            .flatMap(list -> {
                List<DataError> warnings = new ArrayList<>();
                Inventory inventory = holder.getInventory();

                for (int i = 0; i < inventory.getSize() && i < list.size(); i++) {
                    D itemData = list.get(i);
                    String strVal = ops.getStringValue(itemData).orElse("");

                    if ("empty".equals(strVal)) {
                        inventory.setItem(i, null);
                    } else {
                        DataResult<ItemStack> res = Codecs.ITEM_STACK.decode(ops, itemData).prependPath("[" + i + "]");
                        if (res.isError()) {
                            warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                        } else {
                            try {
                                inventory.setItem(i, res.getOrThrow());
                            } catch (Exception e) {
                                warnings.add(DataError.custom("Failed to set item in holder at index " + i + ": " + e.getMessage()));
                            }
                            if (res.isPartial()) warnings.addAll(res.warnings());
                        }
                    }
                }

                return warnings.isEmpty() ? DataResult.success(null) : DataResult.partial(null, warnings);
            });
    }
}