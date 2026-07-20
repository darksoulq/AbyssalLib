package com.github.darksoulq.abyssallib.world.advancement.criterion;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.RecordBuilder;
import com.github.darksoulq.abyssallib.server.event.custom.entity.PlayerStatisticChangeEvent;
import com.github.darksoulq.abyssallib.world.data.statistic.PlayerStatistics;
import com.github.darksoulq.abyssallib.world.data.statistic.Statistic;
import com.github.darksoulq.abyssallib.world.data.statistic.Statistics;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.Set;

/**
 * An advancement criterion tracking the amount of a specific item crafted by the player.
 */
public class ItemCraftedCriterion implements AdvancementCriterion {

    /**
     * The codec used for serializing and deserializing the item crafted criterion.
     */
    public static final Codec<ItemCraftedCriterion> CODEC = RecordBuilder.create(instance -> instance.group(
        Codecs.KEY.fieldOf("item").forGetter(ItemCraftedCriterion.class, p -> p.itemId),
        Codecs.INT.fieldOf("amount").forGetter(ItemCraftedCriterion.class, p -> p.amount)
    ).apply(instance, ItemCraftedCriterion::new)).describe("ItemCraftedCriterion");

    /**
     * The registered type definition for the item crafted criterion.
     */
    public static final CriterionType<ItemCraftedCriterion> TYPE = () -> CODEC;

    private final Key itemId;
    private final int amount;

    /**
     * Constructs a new ItemCraftedCriterion.
     *
     * @param itemId The key of the item to track.
     * @param amount The amount required.
     */
    public ItemCraftedCriterion(Key itemId, int amount) {
        this.itemId = itemId;
        this.amount = amount;
    }

    @Override
    public CriterionType<?> getType() {
        return TYPE;
    }

    /**
     * Checks if the player has crafted the required amount of the target item.
     *
     * @param player The player to evaluate.
     * @return True if the condition is met.
     */
    @Override
    public boolean isMet(Player player) {
        Statistic stat = Statistics.ITEMS_CRAFTED.get(itemId);
        return PlayerStatistics.of(player).get(stat) >= amount;
    }

    @Override
    public Set<Class<? extends Event>> getTargetEvents() {
        return Set.of(PlayerStatisticChangeEvent.class);
    }
}