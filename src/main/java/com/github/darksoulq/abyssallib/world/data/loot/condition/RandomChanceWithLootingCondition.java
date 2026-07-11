package com.github.darksoulq.abyssallib.world.data.loot.condition;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.RecordBuilder;
import com.github.darksoulq.abyssallib.world.data.loot.LootCondition;
import com.github.darksoulq.abyssallib.world.data.loot.LootConditionType;
import com.github.darksoulq.abyssallib.world.data.loot.LootContext;
import org.bukkit.enchantments.Enchantment;

/**
 * A loot condition that evaluates to true based on a probability that increases with the Looting enchantment level.
 * <p>
 * This condition calculates success using the formula: {@code chance + (looting_level * multiplier)}.
 * It is primarily used for rare mob drops where the probability of the drop is intended to scale
 * with the player's equipment.
 * </p>
 */
public class RandomChanceWithLootingCondition extends LootCondition {

    /**
     * The codec used for serializing and deserializing the random chance with looting condition.
     * <p>
     * It maps the "chance" base probability and the "looting_multiplier" float which is
     * added per level of the Looting enchantment.
     * </p>
     */
    public static final Codec<RandomChanceWithLootingCondition> CODEC = RecordBuilder.create(instance -> instance.group(
        Codecs.FLOAT.fieldOf("chance").forGetter(RandomChanceWithLootingCondition.class, p -> p.chance),
        Codecs.FLOAT.fieldOf("looting_multiplier").forGetter(RandomChanceWithLootingCondition.class, p -> p.multiplier)
    ).apply(instance, RandomChanceWithLootingCondition::new)).describe("RandomChanceWithLootingCondition");

    /**
     * The registered type definition for the random chance with looting loot condition.
     */
    public static final LootConditionType<RandomChanceWithLootingCondition> TYPE = () -> CODEC;

    /**
     * The base probability threshold (0.0 to 1.0).
     */
    private final float chance;

    /**
     * The amount added to the base chance per level of Looting.
     */
    private final float multiplier;

    /**
     * Constructs a new RandomChanceWithLootingCondition.
     *
     * @param chance     The base success probability.
     * @param multiplier The probability increase per level of the Looting enchantment.
     */
    public RandomChanceWithLootingCondition(float chance, float multiplier) {
        this.chance = chance;
        this.multiplier = multiplier;
    }

    /**
     * Performs a random check against the base chance plus the looting bonus.
     * <p>
     * The level of the {@link Enchantment#LOOTING} is retrieved from the tool present
     * in the {@link LootContext}. If no tool is present, the level defaults to 0.
     * </p>
     *
     * @param context The {@link LootContext} providing the tool and random source.
     * @return {@code true} if a random float [0, 1) is less than the calculated threshold; {@code false} otherwise.
     */
    @Override
    public boolean test(LootContext context) {
        int level = 0;
        if (context.tool() != null) {
            level = context.tool().getEnchantmentLevel(Enchantment.LOOTING);
        }
        return context.random().nextFloat() < (chance + (level * multiplier));
    }

    /**
     * Retrieves the specific type definition for this loot condition.
     *
     * @return The {@link LootConditionType} associated with {@link RandomChanceWithLootingCondition}.
     */
    @Override
    public LootConditionType<?> getType() {
        return TYPE;
    }
}