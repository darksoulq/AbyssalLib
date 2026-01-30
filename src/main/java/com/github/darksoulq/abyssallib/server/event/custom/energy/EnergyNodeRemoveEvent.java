package com.github.darksoulq.abyssallib.server.event.custom.energy;

import com.github.darksoulq.abyssallib.common.energy.EnergyNode;
import com.github.darksoulq.abyssallib.common.energy.EnergyUnit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Fired when an {@link EnergyNode} is being removed from the {@link com.github.darksoulq.abyssallib.common.energy.EnergyNetwork}.
 * If cancelled, the node remains in the network and its connections are preserved.
 */
public final class EnergyNodeRemoveEvent extends Event implements Cancellable {

    /** The list of handlers for this event. */
    private static final HandlerList HANDLERS = new HandlerList();
    /** The node being removed. */
    private final EnergyNode node;
    /** The native unit of the node being removed. */
    private final EnergyUnit unit;
    /** The cancellation state of the event. */
    private boolean cancelled;

    /**
     * Constructs a new EnergyNodeRemoveEvent.
     *
     * @param node  The {@link EnergyNode} being removed.
     * @param unit  The native {@link EnergyUnit} of the node.
     * @param async Whether the event is being fired asynchronously.
     */
    public EnergyNodeRemoveEvent(EnergyNode node, EnergyUnit unit, boolean async) {
        super(async);
        this.node = node;
        this.unit = unit;
    }

    /** @return The {@link EnergyNode} slated for removal. */
    public EnergyNode getNode() { return node; }

    /** @return The {@link EnergyUnit} associated with the node. */
    public EnergyUnit getUnit() { return unit; }

    /** @return True if the removal is cancelled. */
    @Override
    public boolean isCancelled() { return cancelled; }

    /** @param cancel True to keep the node in the network. */
    @Override
    public void setCancelled(boolean cancel) { this.cancelled = cancel; }

    /** @return The specific {@link HandlerList} for this event. */
    @Override
    public HandlerList getHandlers() { return HANDLERS; }

    /** @return The static {@link HandlerList} required for Bukkit events. */
    public static HandlerList getHandlerList() { return HANDLERS; }
}