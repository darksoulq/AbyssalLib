package com.github.darksoulq.abyssallib.server.bridge.block;

import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.server.bridge.Provider;
import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.mechanics.custom_block.noteblock.NoteBlockMechanic;

public class NexoBlockProvider extends Provider<BridgeBlock<?>> {
    public NexoBlockProvider() {
        super("nexo");
    }

    @Override
    public boolean belongs(BridgeBlock<?> value) {
        return NexoBlocks.isCustomBlock(Identifier
                .of(value.getId().namespace(), value.getId().path()).toString());
    }

    @Override
    public Identifier getId(BridgeBlock<?> value) {
        return Identifier.of(value.getId().namespace(), value.getId().path());
    }

    @Override
    public BridgeBlock<?> get(Identifier id) {
        if (NexoBlocks.isNexoNoteBlock(id.toString()))
            return new BridgeBlock<>(id, getPrefix(), NexoBlocks.noteBlockMechanic(id.toString()));
        if (NexoBlocks.isNexoStringBlock(id.toString()))
            return new BridgeBlock<>(id, getPrefix(), NexoBlocks.stringMechanic(id.toString()));
        if (NexoBlocks.isNexoChorusBlock(id.toString()))
            return new BridgeBlock<>(id, getPrefix(), NexoBlocks.chorusBlockMechanic(id.toString()));
        return null;
    }
}
