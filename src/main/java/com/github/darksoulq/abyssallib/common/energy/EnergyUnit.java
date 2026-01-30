package com.github.darksoulq.abyssallib.common.energy;

import com.github.darksoulq.abyssallib.common.util.Identifier;

/**
 * A record defining an energy measurement unit with its own identifier,
 * symbol, and conversion rate relative to the base unit.
 *
 * @param id             Unique identifier for this unit.
 * @param symbol         Short display name (e.g., "PE", "FE").
 * @param conversionRate The multiplier used to convert this unit into the base unit.
 */
public record EnergyUnit(Identifier id, String symbol, double conversionRate) {
    /** The default unit for AbyssalLib (Paper Energy). */
    public static final EnergyUnit PE = new EnergyUnit(Identifier.of("abyssallib", "pe"), "PE", 1.0);

    /**
     * Converts an amount from this unit to another unit.
     *
     * @param amount The quantity in this unit.
     * @param to     The target unit.
     * @return The converted quantity.
     */
    public double convert(double amount, EnergyUnit to) {
        if (this.equals(to)) return amount;
        return amount * (this.conversionRate / to.conversionRate);
    }

    /**
     * Converts an amount from this unit to the base unit.
     *
     * @param amount The quantity in this unit.
     * @return The base unit equivalent.
     */
    public double toBase(double amount) {
        return amount * conversionRate;
    }

    /**
     * Converts an amount from the base unit into this unit.
     *
     * @param amount The quantity in the base unit.
     * @return The local unit equivalent.
     */
    public double fromBase(double amount) {
        return amount / conversionRate;
    }
}