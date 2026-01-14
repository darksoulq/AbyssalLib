package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types.unique;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
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
    public <D> D serialize(DynamicOps<D> ops, TrialSpawner value) throws Codec.CodecException {
        return CODEC.encode(ops, value.getTrialSpawnerState());
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, BlockData base) throws Codec.CodecException {
        if (!(base instanceof TrialSpawner spawner)) return;
        TrialSpawner.State value = CODEC.decode(ops, input);
        spawner.setTrialSpawnerState(value);
    }
}
