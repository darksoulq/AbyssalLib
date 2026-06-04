package com.github.darksoulq.abyssallib.world.advancement.reward;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.RecordBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * An advancement reward that directly grants a specific {@link ItemStack} to the player.
 */
public class ItemReward implements AdvancementReward {

    /**
     * The codec used for serializing and deserializing the item reward.
     */
    public static final Codec<ItemReward> CODEC = RecordBuilder.create(instance -> instance.group(
        Codecs.ITEM_STACK.fieldOf("item").forGetter(ItemReward.class, p -> p.item)
    ).apply(instance, ItemReward::new)).describe("ItemReward");

    /**
     * The registered type definition for the item reward.
     */
    public static final RewardType<ItemReward> TYPE = () -> CODEC;

    private final ItemStack item;

    /**
     * Constructs a new ItemReward.
     *
     * @param item The stack to distribute.
     */
    public ItemReward(ItemStack item) {
        this.item = item;
    }

    @Override
    public RewardType<?> getType() {
        return TYPE;
    }

    /**
     * Adds the item to the player's inventory, dropping any remainder on the ground.
     *
     * @param player The player receiving the reward.
     */
    @Override
    public void grant(Player player) {
        player.getInventory().addItem(item).values().forEach(remaining ->
            player.getWorld().dropItem(player.getLocation(), remaining)
        );
    }
}