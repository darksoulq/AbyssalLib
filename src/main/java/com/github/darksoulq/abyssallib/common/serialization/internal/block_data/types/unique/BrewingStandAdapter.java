package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types.unique;

import com.github.darksoulq.abyssallib.common.serialization.*;
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
    public <D> DataResult<D> serialize(DynamicOps<D> ops, BrewingStand value) {
        Map<Integer, Boolean> result = new HashMap<>();
        for (int i = 0; i < value.getMaximumBottles(); i++) {
            result.put(i, value.hasBottle(i));
        }
        return CODEC.encode(ops, result);
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, BlockData base) {
        if (!(base instanceof BrewingStand brewingStand))
            return DataResult.error(DataError.custom("Base is not BrewingStand, got: " + base.getClass().getSimpleName()));

        return CODEC.decode(ops, input).flatMap(value -> {
            int max = brewingStand.getMaximumBottles();
            for (Map.Entry<Integer, Boolean> entry : value.entrySet()) {
                int bottle = entry.getKey();
                if (bottle >= max) {
                    return DataResult.error(DataError.custom("Bottle index (" + bottle + ") exceeds maximum (" + (max - 1) + ")"));
                }
                brewingStand.setBottle(bottle, entry.getValue());
            }
            return DataResult.success(null);
        });
    }
}