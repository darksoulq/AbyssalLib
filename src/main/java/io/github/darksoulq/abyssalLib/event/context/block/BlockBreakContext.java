package io.github.darksoulq.abyssalLib.event.context.block;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakContext {
    private final Player player;
    private final Block block;
    private final BlockBreakEvent event;

    public BlockBreakContext(Player player, Block block, BlockBreakEvent event) {
        this.player = player;
        this.block = block;
        this.event = event;
    }

    public Player player() {
        return player;
    }
    public Block block() {
        return block;
    }
    public void cancel() {
        event.setCancelled(true);
    }
}
