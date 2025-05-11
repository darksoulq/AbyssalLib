package com.github.darksoulq.abyssallib.item;

import com.github.darksoulq.abyssallib.registry.BuiltinRegistries;
import com.github.darksoulq.abyssallib.registry.DeferredRegistry;
import com.github.darksoulq.abyssallib.registry.object.DeferredObject;
import org.bukkit.Material;

public class Items {
    public static final DeferredRegistry<Item> ITEMS = DeferredRegistry.create(BuiltinRegistries.ITEMS, "abyssallib");

    public static final DeferredObject<Item> INVISIBLE_ITEM = ITEMS.register("invisible",
            (name, id) -> new Item(id, Material.PAPER));
}
