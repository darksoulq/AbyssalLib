package com.github.darksoulq.abyssallib.server.event.custom.energy;

import com.github.darksoulq.abyssallib.common.energy.EnergyNode;
import com.github.darksoulq.abyssallib.common.energy.EnergyUnit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class EnergyNetworkTransferEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private final EnergyNode source;
    private final EnergyNode target;
    private final EnergyUnit unit;
    private double amount;
    private boolean cancelled;

    public EnergyNetworkTransferEvent(EnergyNode source, EnergyNode target, EnergyUnit unit, double amount, boolean async) {
        super(async);
        this.source = source;
        this.target = target;
        this.unit = unit;
        this.amount = amount;
    }

    public EnergyNode getSource() { return source; }

    public EnergyNode getTarget() { return target; }

    public EnergyUnit getUnit() { return unit; }

    public double getAmount() { return amount; }

    public void setAmount(double amount) { this.amount = amount; }

    @Override
    public boolean isCancelled() { return cancelled; }

    @Override
    public void setCancelled(boolean cancel) { this.cancelled = cancel; }

    @Override
    public HandlerList getHandlers() { return HANDLERS; }

    public static HandlerList getHandlerList() { return HANDLERS; }
}