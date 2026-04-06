package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.TileAdapter;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.TileState;

public class CommandBlockTileAdapter extends TileAdapter<CommandBlock> {

    @Override
    public boolean doesApply(TileState state) {
        return state instanceof CommandBlock;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, CommandBlock value) throws Codec.CodecException {
        return Codecs.STRING.encode(ops, value.getCommand());
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, TileState base) throws Codec.CodecException {
        if (!(base instanceof CommandBlock cmd)) return;
        cmd.setCommand(Codecs.STRING.decode(ops, input));
    }
}