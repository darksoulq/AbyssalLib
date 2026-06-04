package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types.unique;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.TrialSpawner;

public class TrialSpawnerStateAdapter extends Adapter<TrialSpawner> {
    private static final Codec<TrialSpawner.State> CODEC = Codec.enumCodec(TrialSpawner.State.class);

    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof TrialSpawner;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, TrialSpawner value) {
        return CODEC.encode(ops, value.getTrialSpawnerState());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, BlockData base) {
        if (!(base instanceof TrialSpawner spawner))
            return DataResult.error(DataError.custom("Base is not TrialSpawner, got: " + base.getClass().getSimpleName()));

        return CODEC.decode(ops, input).flatMap(value -> {
            spawner.setTrialSpawnerState(value);
            return DataResult.success(null);
        });
    }
}