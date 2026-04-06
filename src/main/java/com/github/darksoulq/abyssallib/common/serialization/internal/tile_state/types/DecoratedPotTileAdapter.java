package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.TileAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.Material;
import org.bukkit.block.DecoratedPot;
import org.bukkit.block.TileState;

import java.util.HashMap;
import java.util.Map;

public class DecoratedPotTileAdapter extends TileAdapter<DecoratedPot> {

    @Override
    public boolean doesApply(TileState state) {
        return state instanceof DecoratedPot;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, DecoratedPot value) throws Codec.CodecException {
        Map<D, D> map = new HashMap<>();
        
        map.put(ops.createString("front"), ops.createString(value.getSherd(DecoratedPot.Side.FRONT).name()));
        map.put(ops.createString("back"), ops.createString(value.getSherd(DecoratedPot.Side.BACK).name()));
        map.put(ops.createString("left"), ops.createString(value.getSherd(DecoratedPot.Side.LEFT).name()));
        map.put(ops.createString("right"), ops.createString(value.getSherd(DecoratedPot.Side.RIGHT).name()));

        return ops.createMap(map);
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, TileState base) throws Codec.CodecException {
        if (!(base instanceof DecoratedPot pot)) return;
        Map<D, D> map = ops.getMap(input).orElseThrow(() -> new Codec.CodecException("Expected map for DecoratedPot"));

        ops.getStringValue(map.get(ops.createString("front"))).ifPresent(s -> Try.run(() -> pot.setSherd(DecoratedPot.Side.FRONT, Material.valueOf(s))));
        ops.getStringValue(map.get(ops.createString("back"))).ifPresent(s -> Try.run(() -> pot.setSherd(DecoratedPot.Side.BACK, Material.valueOf(s))));
        ops.getStringValue(map.get(ops.createString("left"))).ifPresent(s -> Try.run(() -> pot.setSherd(DecoratedPot.Side.LEFT, Material.valueOf(s))));
        ops.getStringValue(map.get(ops.createString("right"))).ifPresent(s -> Try.run(() -> pot.setSherd(DecoratedPot.Side.RIGHT, Material.valueOf(s))));
    }
}