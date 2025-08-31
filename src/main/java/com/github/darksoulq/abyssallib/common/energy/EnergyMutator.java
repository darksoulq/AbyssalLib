package com.github.darksoulq.abyssallib.common.energy;

public interface EnergyMutator extends EnergyView {
    double insert(double amount, Action action);
    double extract(double amount, Action action);
}
