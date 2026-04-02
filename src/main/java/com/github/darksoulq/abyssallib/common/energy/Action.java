package com.github.darksoulq.abyssallib.common.energy;

/**
 * Represents the execution mode of an energy transfer operation.
 */
public enum Action {

    /**
     * Performs the operation and mutates state.
     */
    EXECUTE,

    /**
     * Simulates the operation without modifying state.
     */
    SIMULATE
}