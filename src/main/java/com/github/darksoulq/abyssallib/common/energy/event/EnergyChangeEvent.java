package com.github.darksoulq.abyssallib.common.energy.event;

import com.github.darksoulq.abyssallib.common.energy.EnergyContainer;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EnergyChangeEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final EnergyContainer container;
    private final double previous;
    private final double current;

    public EnergyChangeEvent(EnergyContainer container, double previous, double current) {
        super(!Bukkit.isPrimaryThread());
        this.container = container;
        this.previous = previous;
        this.current = current;
    }

    public EnergyContainer getContainer() { return container; }
    public double getPrevious() { return previous; }
    public double getCurrent() { return current; }
    @Override public HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}
