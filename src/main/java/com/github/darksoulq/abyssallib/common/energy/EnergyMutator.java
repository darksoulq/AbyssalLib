package com.github.darksoulq.abyssallib.common.energy;

/**
 * Represents a modifiable energy container that supports insertion and extraction.
 */
@Deprecated(forRemoval = true)
public interface EnergyMutator extends EnergyView {

    /**
     * Inserts energy into the container.
     *
     * @param amount the amount of energy to insert
     * @param action the action type (simulate or execute)
     * @return the amount of energy actually inserted
     */
    double insert(double amount, Action action);

    /**
     * Extracts energy from the container.
     *
     * @param amount the amount of energy to extract
     * @param action the action type (simulate or execute)
     * @return the amount of energy actually extracted
     */
    double extract(double amount, Action action);
}
