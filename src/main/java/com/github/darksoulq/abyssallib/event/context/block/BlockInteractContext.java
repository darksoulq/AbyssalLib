package com.github.darksoulq.abyssallib.event.context.block;

import com.github.darksoulq.abyssallib.event.context.Context;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**
 * Represents the context of a block interaction event, capturing relevant details
 * such as the player, the block being interacted with, the interaction point, the action performed,
 * the player's hand, and the item being used.
 * <p>
 * This class provides access to the player initiating the interaction, the block being interacted with,
 * the action type, the block face, the point of interaction, and the item being held by the player.
 * </p>
 */
public class BlockInteractContext extends Context<PlayerInteractEvent> {
    /**
     * The player interacting with the block.
     */
    public final Player player;
    /**
     * The bukkit block being interacted with.
     */
    public final Block bukkitBlock;
    /**
     * The block being interacted with, represented as a {@link com.github.darksoulq.abyssallib.block.Block}.
     */
    public final com.github.darksoulq.abyssallib.block.Block block;
    /**
     * The face of the block being interacted with.
     */
    public final BlockFace blockFace;
    /**
     * The location where the interaction occurred.
     */
    public final Location interactionPoint;
    /**
     * The action performed by the player (e.g., right-click, left-click).
     */
    public final Action action;
    /**
     * The hand used by the player (e.g., main hand, off hand).
     */
    public final EquipmentSlot hand;
    /**
     * The item stack the player is holding during the interaction.
     */
    public final ItemStack item;

    /**
     * Constructs a new BlockInteractContext with the given PlayerInteractEvent.
     * Initializes all the fields using the data from the event.
     *
     * @param event The PlayerInteractEvent associated with this context.
     */
    public BlockInteractContext(PlayerInteractEvent event) {
        super(event);
        this.player = event.getPlayer();
        this.bukkitBlock = event.getClickedBlock();
        this.block = com.github.darksoulq.abyssallib.block.Block.from(bukkitBlock);
        this.blockFace = event.getBlockFace();
        this.interactionPoint = event.getInteractionPoint();
        this.action = event.getAction();
        this.hand = event.getHand();
        this.item = event.getItem();
    }
}
