package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types.unique;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.TrialSpawner;

public class TrialSpawnerOminousAdapter extends Adapter<TrialSpawner> {
    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof TrialSpawner;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, TrialSpawner value) {
        return Codecs.BOOLEAN.encode(ops, value.isOminous());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, BlockData base) {
        if (!(base instanceof TrialSpawner spawner))
            return DataResult.error(DataError.custom("Base is not TrialSpawner, got: " + base.getClass().getSimpleName()));

        return Codecs.BOOLEAN.decode(ops, input).flatMap(value -> {
            spawner.setOminous(value);
            return DataResult.success(null);
        });
    }
}