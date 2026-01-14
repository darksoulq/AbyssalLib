package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types.unique;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
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
    public <D> D serialize(DynamicOps<D> ops, Jukebox value) throws Codec.CodecException {
        return Codecs.BOOLEAN.encode(ops, value.hasRecord());
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, BlockData base) throws Codec.CodecException {
        if (!(base instanceof Jukebox jukebox)) return;
        boolean value = Codecs.BOOLEAN.decode(ops, input);
        jukebox.setHasRecord(value);
    }
}
