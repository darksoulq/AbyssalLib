package me.darksoul.abyssalLib.block.test;

import me.darksoul.abyssalLib.AbyssalLib;
import me.darksoul.abyssalLib.block.Block;
import me.darksoul.abyssalLib.registry.BuiltinRegistries;
import me.darksoul.abyssalLib.registry.DeferredRegistry;
import me.darksoul.abyssalLib.registry.RegistryObject;

public class ModBlocks {
    public static final DeferredRegistry<Block> BLOCKS = DeferredRegistry.create(BuiltinRegistries.BLOCKS, AbyssalLib.MODID);

    public static final RegistryObject<Block> TEST_BLOCK = BLOCKS.register("test_block", (name, id) -> new TestBlock(id));
}
