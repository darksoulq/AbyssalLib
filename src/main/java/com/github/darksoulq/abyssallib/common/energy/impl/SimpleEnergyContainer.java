package com.github.darksoulq.abyssallib.common.energy.impl;

import com.github.darksoulq.abyssallib.common.energy.Action;
import com.github.darksoulq.abyssallib.common.energy.EnergyContainer;
import com.github.darksoulq.abyssallib.common.energy.Resizable;
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.RecordCodecBuilder;
import com.github.darksoulq.abyssallib.server.event.EventBus;
import com.github.darksoulq.abyssallib.server.event.custom.energy.EnergyChangeEvent;

/**
 * A simple implementation of {@link EnergyContainer} with mutable energy and capacity.
 * Supports event firing when energy changes and can be serialized using {@link #CODEC}.
 */
@Deprecated(forRemoval = true)
public final class SimpleEnergyContainer implements EnergyContainer, Resizable {

    /**
     * Codec for serializing and deserializing {@link SimpleEnergyContainer}.
     */
    public static final Codec<SimpleEnergyContainer> CODEC = RecordCodecBuilder.create(
            Codecs.DOUBLE.fieldOf("capacity", SimpleEnergyContainer::getCapacity),
            Codecs.DOUBLE.fieldOf("energy", SimpleEnergyContainer::getEnergy),
            SimpleEnergyContainer::new
    );

    private double energy;
    private double capacity;

    /**
     * Creates a new energy container with a given capacity and zero initial energy.
     *
     * @param capacity the maximum energy capacity
     */
    public SimpleEnergyContainer(double capacity) {
        this(capacity, 0.0);
    }

    /**
     * Creates a new energy container with a given capacity and initial energy.
     *
     * @param capacity the maximum energy capacity
     * @param initial  the initial energy stored
     */
    public SimpleEnergyContainer(double capacity, double initial) {
        this.capacity = Math.max(0.0, capacity);
        this.energy = clamp(initial, 0.0, this.capacity);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns the current stored energy.
     */
    @Override
    public double getEnergy() {
        return energy;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns the maximum energy capacity.
     */
    @Override
    public double getCapacity() {
        return capacity;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Inserts energy into the container up to its capacity. Fires an
     * {@link EnergyChangeEvent} if {@code action.execute()} returns true.
     *
     * @param amount the amount of energy to insert
     * @param action the action type (simulate or execute)
     * @return the amount of energy actually inserted
     */
    @Override
    public double insert(double amount, Action action) {
        if (amount <= 0.0 || capacity <= 0.0) return 0.0;

        double space = capacity - energy;
        if (space <= 0.0) return 0.0;

        double toInsert = Math.min(space, amount);

        if (action.execute()) {
            double prev = energy;
            energy += toInsert;
            EventBus.post(new EnergyChangeEvent(this, prev, energy));
        }

        return toInsert;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Extracts energy from the container up to the current stored amount. Fires an
     * {@link EnergyChangeEvent} if {@code action.execute()} returns true.
     *
     * @param amount the amount of energy to extract
     * @param action the action type (simulate or execute)
     * @return the amount of energy actually extracted
     */
    @Override
    public double extract(double amount, Action action) {
        if (amount <= 0.0 || energy <= 0.0) return 0.0;

        double toExtract = Math.min(energy, amount);

        if (action.execute()) {
            double prev = energy;
            energy -= toExtract;
            EventBus.post(new EnergyChangeEvent(this, prev, energy));
        }

        return toExtract;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Sets a new capacity for the container. If the current energy exceeds the new capacity,
     * it is clamped and an {@link EnergyChangeEvent} is fired.
     *
     * @param cap the new capacity (must be non-negative)
     */
    @Override
    public void setCapacity(double cap) {
        double newCap = Math.max(0.0, cap);
        capacity = newCap;

        if (energy > newCap) {
            double prev = energy;
            energy = newCap;
            EventBus.post(new EnergyChangeEvent(this, prev, energy));
        }
    }

    /**
     * Clamps a value to a given range.
     *
     * @param value the value to clamp
     * @param min   the minimum allowed
     * @param max   the maximum allowed
     * @return the clamped value
     */
    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
