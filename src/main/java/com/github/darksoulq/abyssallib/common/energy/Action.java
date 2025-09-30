package com.github.darksoulq.abyssallib.common.energy;

/**
 * Represents the type of action to perform when modifying an energy container.
 * <p>
 * There are two modes:
 * <ul>
 *     <li>{@link #EXECUTE} — actually performs the operation</li>
 *     <li>{@link #SIMULATE} — calculates the result without modifying the container</li>
 * </ul>
 * Useful for predicting outcomes of energy insertion, extraction, or transfer.
 */
public enum Action {
    /** Execute the action and apply changes to the container. */
    EXECUTE,
    /** Simulate the action without modifying the container. */
    SIMULATE;

    /**
     * Checks if this action is {@link #EXECUTE}.
     *
     * @return {@code true} if this action will actually execute, {@code false} otherwise
     */
    public boolean execute() {
        return this == EXECUTE;
    }

    /**
     * Checks if this action is {@link #SIMULATE}.
     *
     * @return {@code true} if this action is a simulation, {@code false} otherwise
     */
    public boolean simulate() {
        return this == SIMULATE;
    }
}
