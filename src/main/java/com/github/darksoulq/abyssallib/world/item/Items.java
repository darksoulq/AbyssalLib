package com.github.darksoulq.abyssallib.world.item;

import com.github.darksoulq.abyssallib.server.registry.DeferredRegistry;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.server.registry.object.Holder;
import org.bukkit.Material;

public class Items {
    public static final DeferredRegistry<Item> ITEMS = DeferredRegistry.create(Registries.ITEMS, "abyssallib");

    public static final Holder<Item> INVISIBLE_ITEM = ITEMS.register("invisible",
            (id) -> new Item(id, Material.STICK));
}
