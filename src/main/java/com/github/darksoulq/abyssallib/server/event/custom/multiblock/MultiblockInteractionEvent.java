package com.github.darksoulq.abyssallib.server.event.custom.multiblock;

import com.github.darksoulq.abyssallib.world.multiblock.Multiblock;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MultiblockInteractionEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final Multiblock multiblock;
    private final Player player;
    private final BlockFace face;
    private final @Nullable Location interaction;
    private final Action action;
    private final @Nullable ItemStack item;
    private boolean cancelled = false;

    public MultiblockInteractionEvent(
            @NotNull Player player,
            @NotNull Multiblock multiblock,
            @NotNull BlockFace face,
            @Nullable Location interaction,
            @NotNull Action action,
            @Nullable ItemStack item
    ) {
        this.player = player;
        this.multiblock = multiblock;
        this.face = face;
        this.interaction = interaction;
        this.action = action;
        this.item = item;
    }

    public @NotNull Multiblock getMultiblock() { return multiblock; }
    public @NotNull Player getPlayer() { return player; }
    public @NotNull BlockFace getBlockFace() { return face; }
    public @Nullable Location getInteractionPoint() { return interaction; }
    public @NotNull Action getAction() { return action; }
    public @Nullable ItemStack getItem() { return item; }

    @Override
    public boolean isCancelled() { return cancelled; }
    @Override
    public void setCancelled(boolean cancel) { this.cancelled = cancel; }
    @Override
    public @NotNull HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}
