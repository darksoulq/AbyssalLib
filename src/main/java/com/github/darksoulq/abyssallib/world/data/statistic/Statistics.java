package com.github.darksoulq.abyssallib.world.data.statistic;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.registry.DeferredRegistry;
import com.github.darksoulq.abyssallib.server.registry.Registries;

public final class Statistics {

    public static final DeferredRegistry<StatisticType> STATISTIC_TYPES = DeferredRegistry.create(Registries.STATISTIC_TYPES, AbyssalLib.PLUGIN_ID);

    public static final StatisticType BLOCKS_MINED = STATISTIC_TYPES.register("blocks_mined", StatisticType::new);
    public static final StatisticType ENTITIES_KILLED = STATISTIC_TYPES.register("entities_killed", StatisticType::new);
    public static final StatisticType ITEMS_CRAFTED = STATISTIC_TYPES.register("items_crafted", StatisticType::new);
}