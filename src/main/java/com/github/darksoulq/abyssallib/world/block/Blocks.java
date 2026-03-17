package com.github.darksoulq.abyssallib.world.block;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.registry.DeferredRegistry;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.block.internal.structure.StructureBlock;

public class Blocks {
    public static final DeferredRegistry<CustomBlock> BLOCKS = DeferredRegistry.create(Registries.BLOCKS, AbyssalLib.PLUGIN_ID);

    public static final CustomBlock STRUCTURE_BLOCK = BLOCKS.register("structure_block", StructureBlock::new);
}
