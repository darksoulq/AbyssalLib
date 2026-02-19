package com.github.darksoulq.abyssallib.world.item.internal;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.registry.DeferredRegistry;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.server.registry.object.Holder;
import com.github.darksoulq.abyssallib.world.block.Blocks;
import com.github.darksoulq.abyssallib.world.item.ItemCategory;
import com.github.darksoulq.abyssallib.world.item.Items;

public class ItemCategories {
    public static final DeferredRegistry<ItemCategory> ITEM_CATEGORIES = DeferredRegistry.create(Registries.ITEM_CATEGORIES, AbyssalLib.PLUGIN_ID);

    public static final Holder<ItemCategory> ITEMS = ITEM_CATEGORIES.register("items", id -> ItemCategory.builder(id)
        .icon(Blocks.STRUCTURE_BLOCK.get().getItem().get().clone())
        .add(Blocks.STRUCTURE_BLOCK.get().getItem().get())
        .build());

    public static final Holder<ItemCategory> GUI_ITEMS = ITEM_CATEGORIES.register("gui_items", id -> ItemCategory.builder(id)
        .icon(Items.CHECKMARK)
        .add(Items.INVISIBLE_ITEM, Items.BACKWARD, Items.CLOSE, Items.FORWARD, Items.CHECKMARK,
            Items.BOUNDING_TOGGLE, Items.NAME_STRUCTURE, Items.INTEGRITY, Items.LOAD_STRUCTURE, Items.SAVE, Items.MIRROR, Items.ROTATE,
            Items.SIZE_X, Items.SIZE_Y, Items.SIZE_Z, Items.X, Items.Y, Items.Z)
        .build());
}
