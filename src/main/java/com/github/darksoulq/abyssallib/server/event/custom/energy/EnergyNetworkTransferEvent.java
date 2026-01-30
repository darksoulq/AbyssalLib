package com.github.darksoulq.abyssallib.server.event.custom.energy;

import com.github.darksoulq.abyssallib.common.energy.EnergyNode;
import com.github.darksoulq.abyssallib.common.energy.EnergyUnit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Fired when energy is being transferred from one {@link EnergyNode} to another within the network.
 * This event allows developers to modify the amount being transferred or cancel the transfer entirely.
 */
public final class EnergyNetworkTransferEvent extends Event implements Cancellable {

    /** The list of handlers for this event. */
    private static final HandlerList HANDLERS = new HandlerList();
    /** The node providing the energy. */
    private final EnergyNode source;
    /** The node receiving the energy. */
    private final EnergyNode target;
    /** The unit in which the transfer amount is measured. */
    private final EnergyUnit unit;
    /** The amount of energy being transferred. */
    private double amount;
    /** The cancellation state of the event. */
    private boolean cancelled;

    /**
     * Constructs a new EnergyNetworkTransferEvent.
     *
     * @param source The {@link EnergyNode} sending energy.
     * @param target The {@link EnergyNode} receiving energy.
     * @param unit   The {@link EnergyUnit} used for the transfer amount.
     * @param amount The quantity of energy to be transferred.
     * @param async  Whether the event is being fired asynchronously.
     */
    public EnergyNetworkTransferEvent(EnergyNode source, EnergyNode target, EnergyUnit unit, double amount, boolean async) {
        super(async);
        this.source = source;
        this.target = target;
        this.unit = unit;
        this.amount = amount;
    }

    /** @return The {@link EnergyNode} acting as the energy source. */
    public EnergyNode getSource() { return source; }

    /** @return The {@link EnergyNode} acting as the energy destination. */
    public EnergyNode getTarget() { return target; }

    /** @return The {@link EnergyUnit} associated with this transfer. */
    public EnergyUnit getUnit() { return unit; }

    /** @return The amount of energy intended for transfer. */
    public double getAmount() { return amount; }

    /** @param amount The new energy amount to set for this transfer. */
    public void setAmount(double amount) { this.amount = amount; }

    /** @return True if the transfer has been cancelled. */
    @Override
    public boolean isCancelled() { return cancelled; }

    /** @param cancel True to prevent the energy transfer from occurring. */
    @Override
    public void setCancelled(boolean cancel) { this.cancelled = cancel; }

    /** @return The specific {@link HandlerList} for this event. */
    @Override
    public HandlerList getHandlers() { return HANDLERS; }

    /** @return The static {@link HandlerList} required for Bukkit events. */
    public static HandlerList getHandlerList() { return HANDLERS; }
}