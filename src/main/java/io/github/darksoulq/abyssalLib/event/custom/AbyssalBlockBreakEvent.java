package io.github.darksoulq.abyssalLib.event.custom;

import io.github.darksoulq.abyssalLib.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class AbyssalBlockBreakEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final Block block;
    private final Player player;
    private boolean cancelled = false;

    public AbyssalBlockBreakEvent(Player player, Block block) {
        this.player = player;
        this.block = block;
    }

    public Block block() {
        return block;
    }
    public Player player() {
        return player;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
}
