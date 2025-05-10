package io.github.darksoulq.abyssalLib.event.custom;

import io.github.darksoulq.abyssalLib.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class AbyssalBlockPlaceEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final Block block;
    private final Player player;
    private final ItemStack item;
    private boolean cancelled = false;

    public AbyssalBlockPlaceEvent(Player player, Block block, ItemStack item) {
        this.player = player;
        this.block = block;
        this.item = item;
    }

    public Block block() {
        return block;
    }
    public Player player() {
        return player;
    }
    public ItemStack item() {
        return item;
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
