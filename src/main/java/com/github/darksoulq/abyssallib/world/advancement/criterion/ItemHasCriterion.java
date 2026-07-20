package com.github.darksoulq.abyssallib.world.advancement.criterion;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.RecordBuilder;
import com.github.darksoulq.abyssallib.server.event.custom.entity.PlayerStatisticChangeEvent;
import com.github.darksoulq.abyssallib.server.event.custom.server.PacketSendEvent;
import com.github.darksoulq.abyssallib.world.item.ItemPredicate;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

/**
 * An advancement criterion ensuring the player has an item matching a specified predicate in their inventory.
 */
public class ItemHasCriterion implements AdvancementCriterion {

    /**
     * The codec used for serializing and deserializing the item has criterion.
     */
    public static final Codec<ItemHasCriterion> CODEC = RecordBuilder.create(instance -> instance.group(
        ItemPredicate.CODEC.fieldOf("predicate").forGetter(ItemHasCriterion.class, p -> p.predicate)
    ).apply(instance, ItemHasCriterion::new)).describe("ItemHasCriterion");

    /**
     * The registered type definition for the item has criterion.
     */
    public static final CriterionType<ItemHasCriterion> TYPE = () -> CODEC;

    private final ItemPredicate predicate;

    /**
     * Constructs a new ItemHasCriterion.
     *
     * @param predicate The predicate defining the required item.
     */
    public ItemHasCriterion(ItemPredicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public CriterionType<?> getType() {
        return TYPE;
    }

    /**
     * Checks if the player's inventory contains at least one item matching the predicate.
     *
     * @param player The player to evaluate.
     * @return True if the condition is met.
     */
    @Override
    public boolean isMet(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && predicate.test(item)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Set<Class<? extends Event>> getTargetEvents() {
        return Set.of(PacketSendEvent.class);
    }
}