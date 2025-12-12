package com.github.darksoulq.abyssallib.server.event.custom.energy;

import com.github.darksoulq.abyssallib.common.energy.EnergyContainer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Deprecated
public final class EnergyTransferEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled;

    private final EnergyContainer source;
    private final EnergyContainer target;
    private final double amount;
    private final boolean simulate;

    public EnergyTransferEvent(EnergyContainer source, EnergyContainer target, double amount, boolean simulate, boolean async) {
        super(async);
        this.source = source;
        this.target = target;
        this.amount = amount;
        this.simulate = simulate;
    }

    public EnergyContainer getSource() {
        return source;
    }

    public EnergyContainer getTarget() {
        return target;
    }

    public double getAmount() {
        return amount;
    }

    public boolean isSimulate() {
        return simulate;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
