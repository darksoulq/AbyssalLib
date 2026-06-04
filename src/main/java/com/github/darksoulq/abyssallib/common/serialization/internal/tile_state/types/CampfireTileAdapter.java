package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.TileAdapter;
import org.bukkit.block.Campfire;
import org.bukkit.block.TileState;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CampfireTileAdapter extends TileAdapter<Campfire> {

    @Override
    public boolean doesApply(TileState state) {
        return state instanceof Campfire;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, Campfire value) {
        List<D> items = new ArrayList<>();
        List<DataError> warnings = new ArrayList<>();

        for (int i = 0; i < value.getSize(); i++) {
            ItemStack item = value.getItem(i);
            if (item != null) {
                DataResult<D> res = Codecs.ITEM_STACK.encode(ops, item).prependPath("[" + i + "]");
                if (res.isError()) {
                    warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    items.add(ops.createMap(Map.of()));
                } else {
                    items.add(res.getOrThrow());
                    if (res.isPartial()) warnings.addAll(res.warnings());
                }
            } else {
                items.add(ops.createMap(Map.of()));
            }
        }

        return warnings.isEmpty() ? DataResult.success(ops.createList(items)) : DataResult.partial(ops.createList(items), warnings);
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, TileState base) {
        if (!(base instanceof Campfire campfire)) return DataResult.success(null);

        return ops.getList(input)
            .map(DataResult::success)
            .orElseGet(() -> DataResult.error(DataError.typeMismatch("List", "Unknown")))
            .flatMap(list -> {
                List<DataError> warnings = new ArrayList<>();
                for (int i = 0; i < campfire.getSize() && i < list.size(); i++) {
                    DataResult<ItemStack> res = Codecs.ITEM_STACK.decode(ops, list.get(i)).prependPath("[" + i + "]");
                    if (res.isError()) {
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    } else {
                        try {
                            campfire.setItem(i, res.getOrThrow());
                        } catch (Exception e) {
                            warnings.add(DataError.custom("Failed to set campfire item at index " + i + ": " + e.getMessage()));
                        }
                        if (res.isPartial()) warnings.addAll(res.warnings());
                    }
                }
                return warnings.isEmpty() ? DataResult.success(null) : DataResult.partial(null, warnings);
            });
    }
}