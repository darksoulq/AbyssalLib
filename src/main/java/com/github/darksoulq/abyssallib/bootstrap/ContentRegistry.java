package com.github.darksoulq.abyssallib.bootstrap;

import com.github.darksoulq.abyssallib.server.permission.internal.PluginPermissions;
import com.github.darksoulq.abyssallib.world.advancement.criterion.Criterions;
import com.github.darksoulq.abyssallib.world.advancement.reward.Rewards;
import com.github.darksoulq.abyssallib.world.block.BlockPredicateLoader;
import com.github.darksoulq.abyssallib.world.block.Blocks;
import com.github.darksoulq.abyssallib.world.data.loot.LootDefaults;
import com.github.darksoulq.abyssallib.world.data.statistic.StatisticFormatters;
import com.github.darksoulq.abyssallib.world.data.statistic.Statistics;
import com.github.darksoulq.abyssallib.world.data.tag.TagTypes;
import com.github.darksoulq.abyssallib.world.entity.EntityPredicateLoader;
import com.github.darksoulq.abyssallib.world.gen.feature.Features;
import com.github.darksoulq.abyssallib.world.gen.feature.tree.FoliagePlacers;
import com.github.darksoulq.abyssallib.world.gen.feature.tree.RootPlacers;
import com.github.darksoulq.abyssallib.world.gen.feature.tree.TreeDecorators;
import com.github.darksoulq.abyssallib.world.gen.feature.tree.TrunkPlacers;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifiers;
import com.github.darksoulq.abyssallib.world.gen.state.provider.BlockStateProviders;
import com.github.darksoulq.abyssallib.world.item.ItemPredicateLoader;
import com.github.darksoulq.abyssallib.world.item.Items;
import com.github.darksoulq.abyssallib.world.item.component.Components;
import com.github.darksoulq.abyssallib.world.item.internal.ItemCategories;
import com.github.darksoulq.abyssallib.world.structure.processor.StructureProcessors;

public final class ContentRegistry {

    public static void init() {
        Components.DATA_COMPONENTS_VANILLA.apply();
        Components.DATA_COMPONENTS.apply();
        ItemPredicateLoader.loadPredicates();
        BlockPredicateLoader.loadPredicates();
        EntityPredicateLoader.loadPredicates();

        Blocks.BLOCKS.apply();
        Items.ITEMS.apply();
        TagTypes.TAG_TYPES.apply();
        Statistics.STATISTIC_TYPES.apply();
        StatisticFormatters.STATISTIC_FORMATTERS.apply();

        BlockAdapters.register();
        TileAdapters.register();
        EntityAdapters.register();

        BlockStateProviders.BLOCK_STATE_PROVIDERS.apply();
        StructureProcessors.STRUCTURE_PROCESSORS.apply();
        PlacementModifiers.PLACEMENT_MODIFIERS.apply();
        Features.FEATURES.apply();
        TrunkPlacers.TRUNK_PLACERS.apply();
        FoliagePlacers.FOLIAGE_PLACERS.apply();
        TreeDecorators.TREE_DECORATORS.apply();
        RootPlacers.ROOT_PLACERS.apply();

        LootDefaults.LOOT_FUNCTION_TYPES.apply();
        LootDefaults.LOOT_CONDITION_TYPES.apply();

        ItemCategories.ITEM_CATEGORIES.apply();
        PluginPermissions.NAMESPACE.apply();

        Criterions.CRITERION.apply();
        Rewards.REWARDS.apply();
    }
}