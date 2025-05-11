package com.github.darksoulq.abyssallib.event.context.item;

import com.github.darksoulq.abyssallib.event.context.Context;
import com.github.darksoulq.abyssallib.item.Item;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

/**
 * Represents the context of an item usage event, capturing details about the player,
 * the item being used, the hand used, the location of the interaction, and any entities or blocks involved.
 * <p>
 * This class serves as a unified context for various types of player events, such as {@link PlayerInteractEvent} and
 * {@link PlayerInteractEntityEvent}, providing the necessary details for handling item interactions.
 * </p>
 */
public class ItemUseContext extends Context<PlayerEvent> {
    /**
     * The player interacting with the item.
     */
    public final Player player;
    /**
     * The item being used by the player.
     */
    public final Item item;
    /**
     * The hand used by the player to interact (e.g., main hand, off hand).
     */
    public final EquipmentSlot hand;
    /**
     * The location where the interaction occurred, if applicable.
     */
    public final Location clickLocation;
    /**
     * The block that was clicked, if applicable.
     */
    public final Block clickedBlock;
    /**
     * The entity that was interacted with, if applicable.
     */
    public final Entity targetEntity;

    /**
     * Constructs a new ItemUseContext with the given PlayerEvent, initializing the context based on the event type.
     *
     * @param event The PlayerEvent (either PlayerInteractEvent or PlayerInteractEntityEvent).
     */
    public ItemUseContext(PlayerEvent event) {
        super(event);
        if (event instanceof PlayerInteractEvent e) {
            this.player = event.getPlayer();
            this.item = Item.from(e.getItem());
            this.hand = e.getHand();
            this.clickLocation = e.getInteractionPoint();
            this.clickedBlock = e.getClickedBlock();
            this.targetEntity = null;
        } else if (event instanceof PlayerInteractEntityEvent e) {
            this.player = event.getPlayer();
            this.item = Item.from(player.getInventory().getItem(e.getHand()));
            this.hand = e.getHand();
            this.clickLocation = null;
            this.clickedBlock = null;
            this.targetEntity = e.getRightClicked();
        } else {
            this.player = null;
            this.item = null;
            this.hand = null;
            this.clickLocation = null;
            this.clickedBlock = null;
            this.targetEntity = null;
        }
    }
}
