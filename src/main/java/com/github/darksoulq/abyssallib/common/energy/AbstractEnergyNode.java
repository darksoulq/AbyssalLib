package com.github.darksoulq.abyssallib.common.energy;

import com.github.darksoulq.abyssallib.server.event.EventBus;
import com.github.darksoulq.abyssallib.server.event.custom.energy.EnergyNodeChangeEvent;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class AbstractEnergyNode implements EnergyNode {
    private final Set<EnergyNode> connections = new CopyOnWriteArraySet<>();
    private double energy;
    private double capacity;

    public AbstractEnergyNode(double capacity, double initial) {
        this.capacity = Math.max(0, capacity);
        this.energy = Math.max(0, Math.min(initial, capacity));
        EnergyNetwork.register(this);
    }

    @Override
    public Set<EnergyNode> getConnections() {
        return connections;
    }

    @Override
    public double insert(double amount) {
        if (amount <= 0 || capacity <= 0) return 0;
        double old = energy;
        double toInsert = Math.min(amount, capacity - energy);
        if (toInsert <= 0) return 0;
        energy += toInsert;
        if (toInsert > 0) EventBus.post(new EnergyNodeChangeEvent(this, old, energy, !org.bukkit.Bukkit.isPrimaryThread()));
        return toInsert;
    }

    @Override
    public double extract(double amount) {
        if (amount <= 0 || energy <= 0) return 0;
        double old = energy;
        double toExtract = Math.min(amount, energy);
        energy -= toExtract;
        if (toExtract > 0) EventBus.post(new EnergyNodeChangeEvent(this, old, energy, !org.bukkit.Bukkit.isPrimaryThread()));
        return toExtract;
    }

    @Override
    public double getEnergy() { return energy; }

    @Override
    public double getCapacity() { return capacity; }

    public void setCapacity(double cap) {
        double old = capacity;
        capacity = Math.max(0, cap);
        if (energy > capacity) {
            double prev = energy;
            energy = capacity;
            EventBus.post(new EnergyNodeChangeEvent(this, prev, energy, !org.bukkit.Bukkit.isPrimaryThread()));
        }
        if (old != capacity) EventBus.post(new EnergyNodeChangeEvent(this, old, capacity, !org.bukkit.Bukkit.isPrimaryThread()));
    }
}
