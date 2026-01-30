package com.github.darksoulq.abyssallib.common.energy;

import com.github.darksoulq.abyssallib.server.event.EventBus;
import com.github.darksoulq.abyssallib.server.event.custom.energy.EnergyNodeChangeEvent;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * A base implementation of {@link EnergyNode} that handles internal storage,
 * connection management, and event firing.
 * <p>
 * This class automatically registers itself to the {@link EnergyNetwork} upon construction.
 */
public abstract class AbstractEnergyNode implements EnergyNode {
    /** A thread-safe set of nodes connected to this node for energy transfer. */
    private final Set<EnergyNode> connections = new CopyOnWriteArraySet<>();
    /** The specific {@link EnergyUnit} this node operates with. */
    private final EnergyUnit unit;
    /** The current amount of energy stored. */
    private double energy;
    /** The maximum energy capacity of this node. */
    private double capacity;

    /**
     * Constructs an EnergyNode with a specific unit.
     *
     * @param capacity The maximum energy capacity.
     * @param initial  The starting energy amount.
     * @param unit     The unit of measurement for this node.
     */
    public AbstractEnergyNode(double capacity, double initial, EnergyUnit unit) {
        this.capacity = Math.max(0, capacity);
        this.energy = Math.max(0, Math.min(initial, capacity));
        this.unit = unit;
        EnergyNetwork.register(this);
    }

    /**
     * Constructs an EnergyNode using the default {@link EnergyUnit#PE}.
     *
     * @param capacity The maximum energy capacity.
     * @param initial  The starting energy amount.
     */
    public AbstractEnergyNode(double capacity, double initial) {
        this(capacity, initial, EnergyUnit.PE);
    }

    /** @return The {@link EnergyUnit} assigned to this node. */
    @Override
    public EnergyUnit getUnit() { return unit; }

    /** @return The set of connected {@link EnergyNode}s. */
    @Override
    public Set<EnergyNode> getConnections() { return connections; }

    /**
     * Attempts to insert energy into the node.
     *
     * @param amount The amount to insert in the node's native unit.
     * @return The actual amount successfully inserted.
     */
    @Override
    public double insert(double amount) {
        if (amount <= 0 || capacity <= 0) return 0;
        double old = energy;
        double toInsert = Math.min(amount, capacity - energy);
        if (toInsert <= 0) return 0;
        energy += toInsert;
        EventBus.post(new EnergyNodeChangeEvent(this, unit, old, energy, !org.bukkit.Bukkit.isPrimaryThread()));
        return toInsert;
    }

    /**
     * Attempts to extract energy from the node.
     *
     * @param amount The amount to extract in the node's native unit.
     * @return The actual amount successfully extracted.
     */
    @Override
    public double extract(double amount) {
        if (amount <= 0 || energy <= 0) return 0;
        double old = energy;
        double toExtract = Math.min(amount, energy);
        energy -= toExtract;
        EventBus.post(new EnergyNodeChangeEvent(this, unit, old, energy, !org.bukkit.Bukkit.isPrimaryThread()));
        return toExtract;
    }

    /** @return The current energy level. */
    @Override
    public double getEnergy() { return energy; }

    /** @return The maximum energy capacity. */
    @Override
    public double getCapacity() { return capacity; }

    /**
     * Updates the maximum capacity. If the current energy exceeds the new capacity,
     * the energy is truncated and a change event is fired.
     *
     * @param cap The new capacity value.
     */
    public void setCapacity(double cap) {
        double old = capacity;
        capacity = Math.max(0, cap);
        if (energy > capacity) {
            double prev = energy;
            energy = capacity;
            EventBus.post(new EnergyNodeChangeEvent(this, unit, prev, energy, !org.bukkit.Bukkit.isPrimaryThread()));
        }
    }
}