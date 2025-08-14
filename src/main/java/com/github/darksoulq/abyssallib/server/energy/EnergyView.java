package com.github.darksoulq.abyssallib.server.energy;

public interface EnergyView {
    double getEnergy();
    double getCapacity();
    default boolean isEmpty() { return getEnergy() <= 0.0; }
    default boolean isFull() { return getEnergy() >= getCapacity(); }
    default double getSpace() {
        double space = getCapacity() - getEnergy();
        return Math.max(space, 0.0);
    }
}
