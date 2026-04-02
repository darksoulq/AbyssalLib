package com.github.darksoulq.abyssallib.common.energy;

import com.github.darksoulq.abyssallib.server.event.EventBus;
import com.github.darksoulq.abyssallib.server.event.custom.energy.EnergyNodeChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Base implementation of {@link EnergyNode} providing standard storage, transfer,
 * and connection handling without automatic registration into {@link EnergyNetwork}.
 *
 * <p>This class manages internal energy storage, directional transfer limits,
 * and connection mappings. Subclasses are responsible for defining behavior
 * such as type identification and optional external registration.</p>
 */
public abstract class AbstractEnergyNode implements EnergyNode {

    /**
     * Mapping of connected neighboring nodes indexed by their relative {@link BlockFace}.
     */
    private final Map<BlockFace, EnergyNode> connections = new ConcurrentHashMap<>();

    /**
     * The energy unit used for all internal calculations and storage.
     */
    private final EnergyUnit unit;

    /**
     * The current amount of stored energy.
     */
    private double energy;

    /**
     * The maximum amount of energy that can be stored.
     */
    private double capacity;

    /**
     * The maximum amount of energy that can be inserted per operation.
     */
    private double maxInsert;

    /**
     * The maximum amount of energy that can be extracted per operation.
     */
    private double maxExtract;

    /**
     * Creates a new energy node with explicit configuration.
     *
     * @param capacity   maximum energy storage capacity
     * @param maxInsert  maximum energy insertion rate per operation
     * @param maxExtract maximum energy extraction rate per operation
     * @param initial    initial stored energy (clamped between {@code 0} and {@code capacity})
     * @param unit       energy unit used for this node
     */
    public AbstractEnergyNode(double capacity, double maxInsert, double maxExtract, double initial, EnergyUnit unit) {
        this.capacity = Math.max(0, capacity);
        this.maxInsert = Math.max(0, maxInsert);
        this.maxExtract = Math.max(0, maxExtract);
        this.energy = Math.max(0, Math.min(initial, this.capacity));
        this.unit = unit;
    }

    /**
     * Creates a new energy node using the default unit {@link EnergyUnits#PE}.
     *
     * @param capacity   maximum energy storage capacity
     * @param maxInsert  maximum energy insertion rate per operation
     * @param maxExtract maximum energy extraction rate per operation
     * @param initial    initial stored energy
     */
    public AbstractEnergyNode(double capacity, double maxInsert, double maxExtract, double initial) {
        this(capacity, maxInsert, maxExtract, initial, EnergyUnits.PE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EnergyUnit getUnit() {
        return unit;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<BlockFace, EnergyNode> getConnections() {
        return connections;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getEnergy() {
        return energy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getCapacity() {
        return capacity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getMaxInsert() {
        return maxInsert;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getMaxExtract() {
        return maxExtract;
    }

    /**
     * Attempts to insert energy into this node.
     *
     * @param side   the side the energy is inserted from, or {@code null}
     * @param amount the amount of energy to insert
     * @param action whether to execute or simulate
     * @return the amount of energy accepted
     */
    @Override
    public double insert(@Nullable BlockFace side, double amount, Action action) {
        if (amount <= 0 || capacity <= 0) return 0;

        double maxPossible = Math.min(amount, maxInsert);
        double toInsert = Math.min(maxPossible, capacity - energy);
        if (toInsert <= 0) return 0;

        if (action == Action.EXECUTE) {
            double old = energy;
            energy += toInsert;
            EventBus.post(new EnergyNodeChangeEvent(this, unit, old, energy, !org.bukkit.Bukkit.isPrimaryThread()));
        }

        return toInsert;
    }

    /**
     * Attempts to extract energy from this node.
     *
     * @param side   the side the energy is extracted from, or {@code null}
     * @param amount the amount of energy to extract
     * @param action whether to execute or simulate
     * @return the amount of energy extracted
     */
    @Override
    public double extract(@Nullable BlockFace side, double amount, Action action) {
        if (amount <= 0 || energy <= 0) return 0;

        double maxPossible = Math.min(amount, maxExtract);
        double toExtract = Math.min(maxPossible, energy);
        if (toExtract <= 0) return 0;

        if (action == Action.EXECUTE) {
            double old = energy;
            energy -= toExtract;
            EventBus.post(new EnergyNodeChangeEvent(this, unit, old, energy, !Bukkit.isPrimaryThread()));
        }

        return toExtract;
    }

    /**
     * Updates the maximum capacity of this node.
     *
     * <p>If the current energy exceeds the new capacity, it will be clamped
     * and a change event will be fired.</p>
     *
     * @param cap the new capacity value
     */
    public void setCapacity(double cap) {
        capacity = Math.max(0, cap);

        if (energy > capacity) {
            double prev = energy;
            energy = capacity;
            EventBus.post(new EnergyNodeChangeEvent(this, unit, prev, energy, !Bukkit.isPrimaryThread()));
        }
    }
}