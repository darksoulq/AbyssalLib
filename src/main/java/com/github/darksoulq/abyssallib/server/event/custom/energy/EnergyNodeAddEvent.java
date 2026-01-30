package com.github.darksoulq.abyssallib.server.event.custom.energy;

import com.github.darksoulq.abyssallib.common.energy.EnergyNode;
import com.github.darksoulq.abyssallib.common.energy.EnergyUnit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Fired when a new {@link EnergyNode} is being registered into the {@link com.github.darksoulq.abyssallib.common.energy.EnergyNetwork}.
 * If cancelled, the node will not be added to the active or global node sets.
 */
public final class EnergyNodeAddEvent extends Event implements Cancellable {

    /** The list of handlers for this event. */
    private static final HandlerList HANDLERS = new HandlerList();
    /** The node being added. */
    private final EnergyNode node;
    /** The native unit of the node being added. */
    private final EnergyUnit unit;
    /** The cancellation state of the event. */
    private boolean cancelled;

    /**
     * Constructs a new EnergyNodeAddEvent.
     *
     * @param node  The {@link EnergyNode} attempting to register.
     * @param unit  The native {@link EnergyUnit} of the node.
     * @param async Whether the event is being fired asynchronously.
     */
    public EnergyNodeAddEvent(EnergyNode node, EnergyUnit unit, boolean async) {
        super(async);
        this.node = node;
        this.unit = unit;
    }

    /** @return The {@link EnergyNode} currently being registered. */
    public EnergyNode getNode() { return node; }

    /** @return The {@link EnergyUnit} of the registered node. */
    public EnergyUnit getUnit() { return unit; }

    /** @return True if the registration is cancelled. */
    @Override
    public boolean isCancelled() { return cancelled; }

    /** @param cancel True to prevent the node from joining the network. */
    @Override
    public void setCancelled(boolean cancel) { this.cancelled = cancel; }

    /** @return The specific {@link HandlerList} for this event. */
    @Override
    public HandlerList getHandlers() { return HANDLERS; }

    /** @return The static {@link HandlerList} required for Bukkit events. */
    public static HandlerList getHandlerList() { return HANDLERS; }
}