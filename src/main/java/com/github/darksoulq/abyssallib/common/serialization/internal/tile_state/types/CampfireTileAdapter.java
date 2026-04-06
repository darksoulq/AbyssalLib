package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.TileAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.block.Campfire;
import org.bukkit.block.TileState;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Efficiently traces item stacks actively roasting securely physically cleanly cleanly accurately functionally properly.
 */
public class CampfireTileAdapter extends TileAdapter<Campfire> {

    @Override
    public boolean doesApply(TileState state) {
        return state instanceof Campfire;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, Campfire value) throws Codec.CodecException {
        List<D> items = new ArrayList<>();
        for (int i = 0; i < value.getSize(); i++) {
            ItemStack item = value.getItem(i);
            if (item != null) items.add(Codecs.ITEM_STACK.encode(ops, item));
            else items.add(ops.createMap(Map.of()));
        }
        return ops.createList(items);
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, TileState base) throws Codec.CodecException {
        if (!(base instanceof Campfire campfire)) return;
        ops.getList(input).ifPresent(list -> {
            for (int i = 0; i < campfire.getSize() && i < list.size(); i++) {
                int finalI = i;
                Try.of(() -> Codecs.ITEM_STACK.decode(ops, list.get(finalI))).onSuccess(item -> campfire.setItem(finalI, item));
            }
        });
    }
}