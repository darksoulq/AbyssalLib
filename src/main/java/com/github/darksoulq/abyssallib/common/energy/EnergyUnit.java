package com.github.darksoulq.abyssallib.common.energy;

import com.github.darksoulq.abyssallib.common.util.Identifier;

public record EnergyUnit(Identifier id, String symbol, double conversionRate) {
    public static final EnergyUnit PE = new EnergyUnit(Identifier.of("abyssallib", "pe"), "PE", 1.0);

    public double convert(double amount, EnergyUnit to) {
        if (this.equals(to)) return amount;
        return amount * (this.conversionRate / to.conversionRate);
    }

    public double toBase(double amount) {
        return amount * conversionRate;
    }

    public double fromBase(double amount) {
        return amount / conversionRate;
    }
}