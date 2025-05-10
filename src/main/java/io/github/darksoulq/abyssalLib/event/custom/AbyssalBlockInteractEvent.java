package io.github.darksoulq.abyssalLib.event.custom;

import io.github.darksoulq.abyssalLib.block.Block;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class AbyssalBlockInteractEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final Block block;
    private final BlockFace face;
    private final Location interaction;
    private final Action action;
    private final ItemStack item;
    private final Player player;
    private boolean cancelled = false;

    public AbyssalBlockInteractEvent(Player player, Block block, BlockFace face, Location interaction, Action action, ItemStack item) {
        this.player = player;
        this.block = block;
        this.face = face;
        this.interaction = interaction;
        this.action = action;
        this.item = item;
    }

    public Block block() {
        return block;
    }
    public Player player() {
        return player;
    }
    public BlockFace blockFace() {
        return face;
    }
    public Location interactionPoint() {
        return interaction;
    }
    public Action action() {
        return action;
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
