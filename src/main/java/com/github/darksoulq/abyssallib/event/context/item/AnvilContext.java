package com.github.darksoulq.abyssallib.event.context.item;

import com.github.darksoulq.abyssallib.event.context.Context;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Represents the context of an anvil preparation event, containing details about the player,
 * the items in the anvil, the resulting item, the rename text, and the repair cost.
 * <p>
 * This class provides access to the player interacting with the anvil, the items involved,
 * and the ability to modify the result and repair cost for the anvil operation.
 * </p>
 */
public class AnvilContext extends Context<PrepareAnvilEvent> {
    /**
     * The player interacting with the anvil.
     */
    public final Player player;
    /**
     * The item in the right slot of the anvil.
     */
    public final ItemStack right;
    /**
     * The item in the left slot of the anvil.
     */
    public final ItemStack left;
    /**
     * The item that would be the result of the anvil operation.
     */
    public final ItemStack result;
    /**
     * The text entered by the player to rename the item.
     */
    public final String renameText;
    /**
     * The repair cost associated with the anvil operation.
     */
    public final int repairCost;

    /**
     * Constructs a new AnvilContext with the given PrepareAnvilEvent.
     * Initializes the context using the event data.
     *
     * @param event The PrepareAnvilEvent associated with this context.
     */
    public AnvilContext(PrepareAnvilEvent event) {
        super(event);
        this.player = (Player) event.getView().getPlayer();
        this.right = event.getView().getTopInventory().getFirstItem();
        this.left = event.getView().getTopInventory().getSecondItem();
        this.result = event.getResult();
        this.renameText = event.getView().getRenameText();
        this.repairCost = event.getView().getRepairCost();
    }

    /**
     * Sets the result of the anvil operation.
     *
     * @param result The item to set as the result of the anvil operation.
     */
    public void result(ItemStack result) {
        event.setResult(result);
    }

    /**
     * Sets the repair cost for the anvil operation.
     *
     * @param levelCost The level cost to set.
     */
    public void repairCost(int levelCost) {
        event.getView().setRepairCost(levelCost);
    }
}
