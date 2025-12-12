package com.github.darksoulq.abyssallib.common.energy;

/**
 * Allows an energy container's capacity to be changed dynamically.
 */
@Deprecated(forRemoval = true)
public interface Resizable {

    /**
     * Sets the maximum energy capacity of this container.
     *
     * @param capacity the new capacity (must be non-negative)
     */
    void setCapacity(double capacity);
}
