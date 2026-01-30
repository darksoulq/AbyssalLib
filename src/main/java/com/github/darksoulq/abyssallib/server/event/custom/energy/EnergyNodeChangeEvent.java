package com.github.darksoulq.abyssallib.server.event.custom.energy;

import com.github.darksoulq.abyssallib.common.energy.EnergyNode;
import com.github.darksoulq.abyssallib.common.energy.EnergyUnit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Fired whenever the internal energy level of an {@link EnergyNode} changes due to insertion,
 * extraction, or capacity adjustments.
 * <p>
 * Note: While this event is {@link Cancellable}, its implementation in the abstract node
 * primarily serves as a notification; logic depends on implementation handling.
 */
public final class EnergyNodeChangeEvent extends Event implements Cancellable {

    /** The list of handlers for this event. */
    private static final HandlerList HANDLERS = new HandlerList();
    /** The node whose energy level changed. */
    private final EnergyNode node;
    /** The unit associated with the energy values. */
    private final EnergyUnit unit;
    /** The energy level before the change. */
    private final double previous;
    /** The new energy level. */
    private final double current;
    /** The cancellation state of the event. */
    private boolean cancelled;

    /**
     * Constructs a new EnergyNodeChangeEvent.
     *
     * @param node     The affected {@link EnergyNode}.
     * @param unit     The {@link EnergyUnit} of the energy values.
     * @param previous The amount of energy held prior to the update.
     * @param current  The new amount of energy held.
     * @param async    Whether the change occurred off the primary server thread.
     */
    public EnergyNodeChangeEvent(EnergyNode node, EnergyUnit unit, double previous, double current, boolean async) {
        super(async);
        this.node = node;
        this.unit = unit;
        this.previous = previous;
        this.current = current;
    }

    /** @return The {@link EnergyNode} that underwent a change. */
    public EnergyNode getNode() { return node; }

    /** @return The {@link EnergyUnit} the change is measured in. */
    public EnergyUnit getUnit() { return unit; }

    /** @return The energy value before the operation. */
    public double getPrevious() { return previous; }

    /** @return The energy value after the operation. */
    public double getCurrent() { return current; }

    /** @return True if the change event is cancelled. */
    @Override
    public boolean isCancelled() { return cancelled; }

    /** @param cancel True to signal that this change should be ignored or reverted. */
    @Override
    public void setCancelled(boolean cancel) { this.cancelled = cancel; }

    /** @return The specific {@link HandlerList} for this event. */
    @Override
    public HandlerList getHandlers() { return HANDLERS; }

    /** @return The static {@link HandlerList} required for Bukkit events. */
    public static HandlerList getHandlerList() { return HANDLERS; }
}