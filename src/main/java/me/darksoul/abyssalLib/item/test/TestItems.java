package me.darksoul.abyssalLib.item.test;

import me.darksoul.abyssalLib.AbyssalLib;
import me.darksoul.abyssalLib.item.Item;
import me.darksoul.abyssalLib.registry.BuiltinRegistries;
import me.darksoul.abyssalLib.registry.DeferredRegistry;
import me.darksoul.abyssalLib.registry.RegistryObject;
import org.bukkit.Material;

public class TestItems {
    public static final DeferredRegistry<Item> ITEMS = DeferredRegistry.create(BuiltinRegistries.ITEMS, AbyssalLib.MODID);

    public static final RegistryObject<Item> MAGIC_WAND = ITEMS.register("magic_wand", (name, id) -> new MagicWand(id));
    public static final RegistryObject<Item> BLOCK_ITEM = ITEMS.register("test_block", (name, id) -> new Item(id, Material.DIRT));
}
