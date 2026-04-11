package com.github.darksoulq.abyssallib.world.data.statistic;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.registry.DeferredRegistry;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.data.statistic.formatter.EntityStatisticsFormatter;
import com.github.darksoulq.abyssallib.world.data.statistic.formatter.ItemStatisticFormatter;

public final class StatisticFormatters {

    public static final DeferredRegistry<StatisticFormatter> STATISTIC_FORMATTERS = DeferredRegistry.create(Registries.STATISTIC_FORMATTERS, AbyssalLib.PLUGIN_ID);

    public static final StatisticFormatter BLOCKS_MINED = STATISTIC_FORMATTERS.register("blocks_mined", key -> new ItemStatisticFormatter());
    public static final StatisticFormatter ITEMS_CRAFTED = STATISTIC_FORMATTERS.register("items_crafted", key -> new ItemStatisticFormatter());
    public static final StatisticFormatter ENTITIES_KILLED = STATISTIC_FORMATTERS.register("entities_killed", key -> new EntityStatisticsFormatter());
}