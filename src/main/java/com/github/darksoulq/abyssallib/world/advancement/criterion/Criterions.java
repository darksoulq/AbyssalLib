package com.github.darksoulq.abyssallib.world.advancement.criterion;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.registry.DeferredRegistry;
import com.github.darksoulq.abyssallib.server.registry.Registries;

public class Criterions {
    public static final DeferredRegistry<CriterionType<?>> CRITERION = DeferredRegistry.create(Registries.CRITERION, AbyssalLib.PLUGIN_ID);

    public static final CriterionType<?> AUTO_GRANT = CRITERION.register("auto_grant", id -> AutoGrantCriterion.TYPE);
    public static final CriterionType<?> HAS_ITEM = CRITERION.register("has_item", id -> ItemHasCriterion.TYPE);
    public static final CriterionType<?> STATISTIC = CRITERION.register("statistic", id -> StatisticCriterion.TYPE);
    public static final CriterionType<?> LEVEL = CRITERION.register("level", id -> LevelCriterion.TYPE);
    public static final CriterionType<?> CUSTOM_STATISTIC = CRITERION.register("custom_statistic", id -> CustomStatisticCriterion.TYPE);
    public static final CriterionType<?> CUSTOM_ATTRIBUTE = CRITERION.register("custom_attribute", id -> CustomAttributeCriterion.TYPE);
    public static final CriterionType<?> ITEM_CRAFTED = CRITERION.register("item_crafted", id -> ItemCraftedCriterion.TYPE);
    public static final CriterionType<?> ENTITY_KILLED = CRITERION.register("entity_killed", id -> EntityKilledCriterion.TYPE);
    public static final CriterionType<?> BLOCK_MINED = CRITERION.register("block_mined", id -> BlockMinedCriterion.TYPE);
    public static final CriterionType<?> LOCATION = CRITERION.register("location", id -> LocationCriterion.TYPE);
    public static final CriterionType<?> POTION_EFFECT = CRITERION.register("potion_effect", id -> PotionEffectCriterion.TYPE);
}