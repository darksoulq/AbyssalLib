package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.TileAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.block.BrushableBlock;
import org.bukkit.block.TileState;

import java.util.HashMap;
import java.util.Map;

public class BrushableBlockTileAdapter extends TileAdapter<BrushableBlock> {

    @Override
    public boolean doesApply(TileState state) {
        return state instanceof BrushableBlock;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, BrushableBlock value) throws Codec.CodecException {
        Map<D, D> map = new HashMap<>();
        if (!value.getItem().isEmpty()) {
            map.put(ops.createString("item"), Codecs.ITEM_STACK.encode(ops, value.getItem()));
        }
        return ops.createMap(map);
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, TileState base) throws Codec.CodecException {
        if (!(base instanceof BrushableBlock brushable)) return;
        Map<D, D> map = ops.getMap(input).orElseThrow(() -> new Codec.CodecException("Expected map for BrushableBlock"));

        D itemData = map.get(ops.createString("item"));
        if (itemData != null) {
            Try.of(() -> Codecs.ITEM_STACK.decode(ops, itemData)).onSuccess(brushable::setItem);
        }
    }
}