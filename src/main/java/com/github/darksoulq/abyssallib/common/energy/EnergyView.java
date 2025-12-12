package com.github.darksoulq.abyssallib.common.energy;

/**
 * Represents a read-only view of an energy container.
 * Provides information about the current energy level and capacity.
 */
@Deprecated(forRemoval = true)
public interface EnergyView {

    /**
     * Gets the current energy stored in this container.
     *
     * @return the current energy
     */
    double getEnergy();

    /**
     * Gets the maximum energy capacity of this container.
     *
     * @return the energy capacity
     */
    double getCapacity();

    /**
     * Checks whether the container is empty.
     *
     * @return {@code true} if the energy is zero or less, otherwise {@code false}
     */
    default boolean isEmpty() {
        return getEnergy() <= 0.0;
    }

    /**
     * Checks whether the container is full.
     *
     * @return {@code true} if the energy is greater than or equal to capacity, otherwise {@code false}
     */
    default boolean isFull() {
        return getEnergy() >= getCapacity();
    }

    /**
     * Gets the remaining space available in the container.
     *
     * @return the difference between capacity and current energy, never negative
     */
    default double getSpace() {
        double space = getCapacity() - getEnergy();
        return Math.max(space, 0.0);
    }
}
