package com.github.darksoulq.abyssallib.world.data.loot.condition;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.RecordBuilder;
import com.github.darksoulq.abyssallib.world.data.loot.LootCondition;
import com.github.darksoulq.abyssallib.world.data.loot.LootConditionType;
import com.github.darksoulq.abyssallib.world.data.loot.LootContext;
import com.github.darksoulq.abyssallib.world.item.ItemPredicate;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * A loot condition that evaluates to true if the tool used in the generation context
 * matches a specific material.
 * <p>
 * This is primarily used for block-break loot tables where specific items should
 * only drop when harvested with a specific tool (e.g., Shears for leaves).
 * </p>
 */
public class MatchToolCondition extends LootCondition {

    /**
     * The codec used for serializing and deserializing the match tool condition.
     * <p>
     * It maps the "item" field to a string representation of a {@link Material}.
     * </p>
     */
    public static final Codec<MatchToolCondition> CODEC = RecordBuilder.create(instance -> instance.group(
        ItemPredicate.CODEC.fieldOf("item").forGetter(MatchToolCondition.class, p -> p.item)
    ).apply(instance, MatchToolCondition::new)).describe("MatchToolCondition");

    /**
     * The registered type definition for the match tool loot condition.
     */
    public static final LootConditionType<MatchToolCondition> TYPE = () -> CODEC;

    /**
     * The {@link Material} required for the tool to satisfy this condition.
     */
    private final ItemPredicate item;

    /**
     * Constructs a new MatchToolCondition for the specified material.
     *
     * @param item The {@link Material} to match against the context tool.
     */
    public MatchToolCondition(ItemPredicate item) {
        this.item = item;
    }

    /**
     * Tests whether the tool in the provided {@link LootContext} matches the required material.
     *
     * @param context The {@link LootContext} providing the tool {@link ItemStack}.
     * @return {@code true} if the tool is present and matches the material; {@code false} otherwise.
     */
    @Override
    public boolean test(LootContext context) {
        ItemStack tool = context.tool();
        return tool != null && item.test(tool);
    }

    /**
     * Retrieves the specific type definition for this loot condition.
     *
     * @return The {@link LootConditionType} associated with {@link MatchToolCondition}.
     */
    @Override
    public LootConditionType<?> getType() {
        return TYPE;
    }
}