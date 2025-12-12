package com.github.darksoulq.abyssallib.server.event.custom.energy;

import com.github.darksoulq.abyssallib.common.energy.EnergyNode;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class EnergyNodeAddEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private final EnergyNode node;
    private boolean cancelled;

    public EnergyNodeAddEvent(EnergyNode node, boolean async) {
        super(async);
        this.node = node;
    }

    public EnergyNode getNode() { return node; }

    @Override
    public boolean isCancelled() { return cancelled; }

    @Override
    public void setCancelled(boolean cancel) { this.cancelled = cancel; }

    @Override
    public HandlerList getHandlers() { return HANDLERS; }

    public static HandlerList getHandlerList() { return HANDLERS; }
}
