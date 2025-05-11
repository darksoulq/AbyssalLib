package me.darksoul.abyssallib.item;

import me.darksoul.abyssallib.registry.BuiltinRegistries;
import me.darksoul.abyssallib.registry.DeferredRegistry;
import me.darksoul.abyssallib.registry.object.DeferredObject;
import org.bukkit.Material;

public class Items {
    public static final DeferredRegistry<Item> ITEMS = DeferredRegistry.create(BuiltinRegistries.ITEMS, "abyssallib");

    public static final DeferredObject<Item> INVISIBLE_ITEM = ITEMS.register("invisible",
            (name, id) -> new Item(id, Material.PAPER));
}
