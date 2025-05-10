package io.github.darksoulq.abyssalLib.item;

import io.github.darksoulq.abyssalLib.registry.BuiltinRegistries;
import io.github.darksoulq.abyssalLib.registry.DeferredRegistry;
import io.github.darksoulq.abyssalLib.registry.object.DeferredObject;
import org.bukkit.Material;

public class Items {
    public static final DeferredRegistry<Item> ITEMS = DeferredRegistry.create(BuiltinRegistries.ITEMS, "abyssallib");

    public static final DeferredObject<Item> INVISIBLE_ITEM = ITEMS.register("invisible",
            (name, id) -> new Item(id, Material.PAPER));
}
