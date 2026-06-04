package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
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
    public <D> DataResult<D> serialize(DynamicOps<D> ops, CommandBlock value) {
        return Codecs.STRING.encode(ops, value.getCommand());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, TileState base) {
        if (!(base instanceof CommandBlock cmd)) return DataResult.success(null);

        return Codecs.STRING.decode(ops, input).flatMap(str -> {
            cmd.setCommand(str);
            return DataResult.success(null);
        });
    }
}