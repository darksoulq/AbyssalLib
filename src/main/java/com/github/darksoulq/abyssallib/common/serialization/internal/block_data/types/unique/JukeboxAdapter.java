package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types.unique;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Jukebox;

public class JukeboxAdapter extends Adapter<Jukebox> {
    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof Jukebox;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, Jukebox value) {
        return Codecs.BOOLEAN.encode(ops, value.hasRecord());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, BlockData base) {
        if (!(base instanceof Jukebox jukebox))
            return DataResult.error(DataError.custom("Base is not Jukebox, got: " + base.getClass().getSimpleName()));

        return Codecs.BOOLEAN.decode(ops, input).flatMap(value -> {
            jukebox.setHasRecord(value);
            return DataResult.success(null);
        });
    }
}