package com.github.darksoulq.abyssallib.server.event.custom.energy;

import com.github.darksoulq.abyssallib.common.energy.EnergyNode;
import com.github.darksoulq.abyssallib.common.energy.EnergyUnit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class EnergyNodeChangeEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private final EnergyNode node;
    private final EnergyUnit unit;
    private final double previous;
    private final double current;
    private boolean cancelled;

    public EnergyNodeChangeEvent(EnergyNode node, EnergyUnit unit, double previous, double current, boolean async) {
        super(async);
        this.node = node;
        this.unit = unit;
        this.previous = previous;
        this.current = current;
    }

    public EnergyNode getNode() { return node; }

    public EnergyUnit getUnit() { return unit; }

    public double getPrevious() { return previous; }

    public double getCurrent() { return current; }

    @Override
    public boolean isCancelled() { return cancelled; }

    @Override
    public void setCancelled(boolean cancel) { this.cancelled = cancel; }

    @Override
    public HandlerList getHandlers() { return HANDLERS; }

    public static HandlerList getHandlerList() { return HANDLERS; }
}