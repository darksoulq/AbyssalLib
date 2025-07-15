package com.github.darksoulq.abyssallib.world.level.item;

import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.server.registry.DeferredRegistry;
import com.github.darksoulq.abyssallib.server.registry.object.DeferredObject;
import org.bukkit.Material;

public class Items {
    public static final DeferredRegistry<Item> ITEMS = DeferredRegistry.create(Registries.ITEMS, "abyssallib");

    public static final DeferredObject<Item> INVISIBLE_ITEM = ITEMS.register("invisible",
            (id) -> new Item(id, Material.STICK));
}
