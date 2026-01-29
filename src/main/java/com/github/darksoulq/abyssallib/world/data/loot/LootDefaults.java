package com.github.darksoulq.abyssallib.world.data.loot;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.registry.DeferredRegistry;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.server.registry.object.Holder;
import com.github.darksoulq.abyssallib.world.data.loot.condition.*;
import com.github.darksoulq.abyssallib.world.data.loot.function.*;

public class LootDefaults {
    public static final DeferredRegistry<LootFunctionType<?>> LOOT_FUNCTION_TYPES = DeferredRegistry.create(Registries.LOOT_FUNCTIONS, AbyssalLib.PLUGIN_ID);
    public static final DeferredRegistry<LootConditionType<?>> LOOT_CONDITION_TYPES = DeferredRegistry.create(Registries.LOOT_CONDITIONS, AbyssalLib.PLUGIN_ID);

    public static final Holder<LootFunctionType<?>> FURNACE_SMELT = LOOT_FUNCTION_TYPES.register("furnace_smelt", id -> FurnaceSmeltFunction.TYPE);
    public static final Holder<LootFunctionType<?>> LOOTING_ENCHANT = LOOT_FUNCTION_TYPES.register("looting_enchant", id -> LootingEnchantFunction.TYPE);
    public static final Holder<LootFunctionType<?>> SET_COUNT = LOOT_FUNCTION_TYPES.register("set_count", id -> SetCountFunction.TYPE);
    public static final Holder<LootFunctionType<?>> SET_LORE = LOOT_FUNCTION_TYPES.register("set_lore", id -> SetLoreFunction.TYPE);
    public static final Holder<LootFunctionType<?>> SET_NAME = LOOT_FUNCTION_TYPES.register("set_name", id -> SetNameFunction.TYPE);
    public static final Holder<LootFunctionType<?>> EXPLOSION_DECAY = LOOT_FUNCTION_TYPES.register("explosion_decay", id -> ExplosionDecayFunction.TYPE);
    public static final Holder<LootFunctionType<?>> LIMIT_COUNT = LOOT_FUNCTION_TYPES.register("limit_count", id -> LimitCountFunction.TYPE);
    public static final Holder<LootFunctionType<?>> SET_DAMAGE = LOOT_FUNCTION_TYPES.register("set_damage", id -> SetDamageFunction.TYPE);
    public static final Holder<LootFunctionType<?>> ENCHANT_RANDOMLY = LOOT_FUNCTION_TYPES.register("enchant_randomly", id -> EnchantRandomlyFunction.TYPE);

    public static final Holder<LootConditionType<?>> KILLED_BY_PLAYER = LOOT_CONDITION_TYPES.register("killed_by_player", id -> KilledByPlayerCondition.TYPE);
    public static final Holder<LootConditionType<?>> LOCATION_CHECK = LOOT_CONDITION_TYPES.register("location_check", id -> LocationCheckCondition.TYPE);
    public static final Holder<LootConditionType<?>> RANDOM_CHANCE = LOOT_CONDITION_TYPES.register("random_chance", id -> RandomChanceCondition.TYPE);
    public static final Holder<LootConditionType<?>> RANDOM_CHANCE_WITH_LOOTING = LOOT_CONDITION_TYPES.register("random_chance_with_looting", id -> RandomChanceWithLootingCondition.TYPE);
    public static final Holder<LootConditionType<?>> SURVIVES_EXPLOSION = LOOT_CONDITION_TYPES.register("survives_explosion", id -> SurvivesExplosionCondition.TYPE);
    public static final Holder<LootConditionType<?>> MATCH_TOOL = LOOT_CONDITION_TYPES.register("match_tool", id -> MatchToolCondition.TYPE);
}