package com.github.darksoulq.abyssallib.event.context.block;

import com.github.darksoulq.abyssallib.event.context.Context;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
/**
 * Represents the context of a block break event, encapsulating the relevant details
 * about the player, the block being broken, and the event itself.
 * <p>
 * This class provides access to the player involved, the block being broken,
 * </p>
 */
public class BlockBreakContext extends Context<BlockBreakEvent> {
    /**
     * The player who is breaking the block.
     */
    public final Player player;
    /**
     * The bukkit block being broken.
     */
    public final Block bukkitBlock;
    /**
     * The block being broken, converted to a {@link com.github.darksoulq.abyssallib.block.Block} representation.
     */
    public final com.github.darksoulq.abyssallib.block.Block block;
    /**
     * The amount of experience to drop when the block is broken.
     */
    public final int expToDrop;

    /**
     * Constructs a new BlockBreakContext with the given BlockBreakEvent.
     * Initializes fields like player, block, cancellation status, and experience to drop.
     *
     * @param event The BlockBreakEvent that this context represents.
     */
    public BlockBreakContext(BlockBreakEvent event) {
        super(event);
        this.player = event.getPlayer();
        this.bukkitBlock = event.getBlock();
        this.block = com.github.darksoulq.abyssallib.block.Block.from(bukkitBlock);
        this.expToDrop = event.getExpToDrop();
    }
}
