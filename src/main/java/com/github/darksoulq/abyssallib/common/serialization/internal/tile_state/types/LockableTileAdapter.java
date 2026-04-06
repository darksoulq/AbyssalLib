package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.TileAdapter;
import org.bukkit.block.Lockable;
import org.bukkit.block.TileState;

public class LockableTileAdapter extends TileAdapter<Lockable> {

    @Override
    public boolean doesApply(TileState state) {
        return state instanceof Lockable lockable && lockable.isLocked();
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, Lockable value) throws Codec.CodecException {
        return Codecs.STRING.encode(ops, value.getLock());
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, TileState base) throws Codec.CodecException {
        if (!(base instanceof Lockable lockable)) return;
        lockable.setLock(Codecs.STRING.decode(ops, input));
    }
}