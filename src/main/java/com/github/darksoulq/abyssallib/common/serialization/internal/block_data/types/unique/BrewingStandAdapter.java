package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types.unique;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.BrewingStand;

import java.util.HashMap;
import java.util.Map;

public class BrewingStandAdapter extends Adapter<BrewingStand> {
    private static final Codec<Map<Integer, Boolean>> CODEC = Codec.map(Codecs.INT, Codecs.BOOLEAN);
    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof BrewingStand;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, BrewingStand value) throws Codec.CodecException {
        Map<Integer, Boolean> result = new HashMap<>();
        for (int i = 0; i < value.getMaximumBottles(); i++) {
            result.put(i, value.hasBottle(i));
        }
        return CODEC.encode(ops, result);
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, BlockData base) throws Codec.CodecException {
        if (!(base instanceof BrewingStand brewingStand)) return;
        Map<Integer, Boolean> value = CODEC.decode(ops, input);
        int max = brewingStand.getMaximumBottles();

        value.forEach((bottle, state) -> {
            if (bottle > max) return;
            brewingStand.setBottle(bottle, state);
        });
    }
}
