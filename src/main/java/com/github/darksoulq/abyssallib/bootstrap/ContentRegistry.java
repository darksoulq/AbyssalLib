package com.github.darksoulq.abyssallib.bootstrap;

import com.github.darksoulq.abyssallib.world.block.Blocks;
import com.github.darksoulq.abyssallib.world.data.loot.LootDefaults;
import com.github.darksoulq.abyssallib.world.data.tag.TagTypes;
import com.github.darksoulq.abyssallib.world.gen.feature.Features;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifiers;
import com.github.darksoulq.abyssallib.world.item.ItemPredicateLoader;
import com.github.darksoulq.abyssallib.world.item.Items;
import com.github.darksoulq.abyssallib.world.item.component.Components;
import com.github.darksoulq.abyssallib.world.item.internal.ItemCategories;
import com.github.darksoulq.abyssallib.server.permission.internal.PluginPermissions;

public final class ContentRegistry {

    public static void registerAll() {
        Components.DATA_COMPONENTS_VANILLA.apply();
        Components.DATA_COMPONENTS.apply();
        ItemPredicateLoader.loadPredicates();

        Blocks.BLOCKS.apply();
        Items.ITEMS.apply();
        TagTypes.TAG_TYPES.apply();

        BlockAdapterRegistry.register();

        PlacementModifiers.PLACEMENT_MODIFIERS.apply();
        Features.FEATURES.apply();
        LootDefaults.LOOT_FUNCTION_TYPES.apply();
        LootDefaults.LOOT_CONDITION_TYPES.apply();

        ItemCategories.ITEM_CATEGORIES.apply();
        PluginPermissions.NAMESPACE.apply();
    }
}