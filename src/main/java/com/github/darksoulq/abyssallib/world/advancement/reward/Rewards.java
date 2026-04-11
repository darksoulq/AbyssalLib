package com.github.darksoulq.abyssallib.world.advancement.reward;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.registry.DeferredRegistry;
import com.github.darksoulq.abyssallib.server.registry.Registries;

public class Rewards {
    public static final DeferredRegistry<RewardType<?>> REWARDS = DeferredRegistry.create(Registries.REWARDS, AbyssalLib.PLUGIN_ID);

    public static final RewardType<?> ITEM = REWARDS.register("item", id -> ItemReward.TYPE);
    public static final RewardType<?> EXPERIENCE = REWARDS.register("experience", id -> ExperienceReward.TYPE);
    public static final RewardType<?> COMMAND = REWARDS.register("command", id -> CommandReward.TYPE);
    public static final RewardType<?> POTION_EFFECT = REWARDS.register("potion_effect", id -> PotionEffectReward.TYPE);
    public static final RewardType<?> LOOT_TABLE = REWARDS.register("loot_table", id -> LootTableReward.TYPE);
    public static final RewardType<?> CUSTOM_LOOT_TABLE = REWARDS.register("custom_loot_table", id -> CustomLootTableReward.TYPE);
}