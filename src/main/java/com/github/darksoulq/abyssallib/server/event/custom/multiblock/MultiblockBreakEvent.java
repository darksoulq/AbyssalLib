package com.github.darksoulq.abyssallib.server.event.custom.multiblock;

import com.github.darksoulq.abyssallib.world.multiblock.Multiblock;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MultiblockBreakEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final Cause cause;
    private final Multiblock multiblock;
    private final @Nullable Entity eCause;
    private final @Nullable Block bCause;
    private final @Nullable Player player;
    private final @Nullable ItemStack tool;
    private boolean cancelled = false;

    public MultiblockBreakEvent(@Nullable Player player, @NotNull Multiblock multiblock, @Nullable ItemStack tool) {
        this.cause = Cause.PLAYER;
        this.eCause = null;
        this.bCause = null;
        this.player = player;
        this.multiblock = multiblock;
        this.tool = tool;
    }
    public MultiblockBreakEvent(@Nullable Entity entity, @NotNull Multiblock multiblock) {
        this.cause = Cause.ENTITY_EXPLODE;
        this.eCause = entity;
        this.bCause = null;
        this.player = null;
        this.multiblock = multiblock;
        this.tool = null;
    }
    public MultiblockBreakEvent(@Nullable Block block, @NotNull Multiblock multiblock) {
        this.cause = Cause.BLOCK_EXPLODE;
        this.eCause = null;
        this.bCause = block;
        this.player = null;
        this.multiblock = multiblock;
        this.tool = null;
    }

    public @NotNull Multiblock getMultiblock() { return multiblock; }
    public @Nullable Player getPlayer() { return player; }
    public @Nullable Block getExplodingBlock() {
        return bCause;
    }
    public @Nullable Entity getExplodingEntity() {
        return eCause;
    }
    public @Nullable ItemStack getTool() { return tool; }

    @Override
    public boolean isCancelled() { return cancelled; }
    @Override
    public void setCancelled(boolean cancel) { this.cancelled = cancel; }
    @Override
    public @NotNull HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }

    public enum Cause {
        PLAYER,
        ENTITY_EXPLODE,
        BLOCK_EXPLODE
    }
}
