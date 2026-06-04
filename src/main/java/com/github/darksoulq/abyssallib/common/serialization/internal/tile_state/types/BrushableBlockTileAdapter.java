package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.TileAdapter;
import org.bukkit.block.BrushableBlock;
import org.bukkit.block.TileState;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class BrushableBlockTileAdapter extends TileAdapter<BrushableBlock> {

    @Override
    public boolean doesApply(TileState state) {
        return state instanceof BrushableBlock;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, BrushableBlock value) {
        Map<D, D> map = new HashMap<>();

        if (!value.getItem().isEmpty()) {
            DataResult<D> res = Codecs.ITEM_STACK.encode(ops, value.getItem()).prependPath("item");
            if (res.isError()) return DataResult.error(res.error().get());
            map.put(ops.createString("item"), res.getOrThrow());
            if (res.isPartial()) return DataResult.partial(ops.createMap(map), res.warnings());
        }

        return DataResult.success(ops.createMap(map));
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, TileState base) {
        if (!(base instanceof BrushableBlock brushable)) return DataResult.success(null);

        return ops.getMap(input)
            .map(DataResult::success)
            .orElseGet(() -> DataResult.error(DataError.typeMismatch("Map", "Unknown")))
            .flatMap(map -> {
                D itemData = map.get(ops.createString("item"));
                if (itemData != null) {
                    DataResult<ItemStack> res = Codecs.ITEM_STACK.decode(ops, itemData).prependPath("item");
                    if (res.isError()) return DataResult.error(res.error().get());
                    try {
                        brushable.setItem(res.getOrThrow());
                    } catch (Exception e) {
                        return DataResult.error(DataError.custom("Failed to set brushable block item: " + e.getMessage()));
                    }
                    return res.isPartial() ? DataResult.partial(null, res.warnings()) : DataResult.success(null);
                }
                return DataResult.success(null);
            });
    }
}