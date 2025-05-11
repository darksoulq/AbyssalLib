package me.darksoul.abyssallib.event.context.block;

import me.darksoul.abyssallib.event.context.Context;
import me.darksoul.abyssallib.item.Item;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;

/**
 * Represents the context of a block placement event, encapsulating the relevant details
 * about the player, the block being placed, the item used for placement, the hand used,
 * and the event itself.
 * <p>
 * This class provides access to the player who placed the block, the block being placed,
 * the item used to place it, the hand the player used, and the BlockPlaceEvent status.
 * </p>
 */
public class BlockPlaceContext extends Context<BlockPlaceEvent> {
    /**
     * The bukkit block being placed.
     */
    public final Block bukkitBlock;
    /**
     * The block being placed, converted to a {@link me.darksoul.abyssallib.block.Block} representation.
     */
    public final me.darksoul.abyssallib.block.Block block;
    /**
     * The player placing the block.
     */
    public final Player player;
    /**
     * The item being used to place the block.
     */
    public final Item item;
    /**
     * The hand used by the player to place the block (e.g., main hand, off hand).
     */
    public final EquipmentSlot hand;

    /**
     * Constructs a new BlockPlaceContext with the given BlockPlaceEvent.
     * Initializes fields like the block, player, item, hand, and event.
     *
     * @param event The BlockPlaceEvent that this context represents.
     */
    public BlockPlaceContext(BlockPlaceEvent event) {
        super(event);
        this.bukkitBlock = event.getBlock();
        this.block = me.darksoul.abyssallib.block.Block.from(bukkitBlock);
        this.player = event.getPlayer();
        this.item = Item.from(event.getItemInHand());
        this.hand = event.getHand();
    }
}
