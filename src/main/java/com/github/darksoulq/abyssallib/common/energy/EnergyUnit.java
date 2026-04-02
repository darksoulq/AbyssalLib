package com.github.darksoulq.abyssallib.common.energy;

import net.kyori.adventure.key.Key;

/**
 * Represents a unit of energy with a conversion rate relative to a base unit.
 *
 * @param id             unique identifier of the unit
 * @param symbol         display symbol (e.g. FE, RF)
 * @param conversionRate multiplier relative to the base unit
 */
public record EnergyUnit(Key id, String symbol, double conversionRate) {

    /**
     * Converts an amount from this unit into another unit.
     *
     * @param amount the value in this unit
     * @param to     the target unit
     * @return converted value in the target unit
     */
    public double convert(double amount, EnergyUnit to) {
        if (this.equals(to)) return amount;
        return amount * (this.conversionRate / to.conversionRate);
    }

    /**
     * Converts a value into the base unit.
     *
     * @param amount value in this unit
     * @return value in base unit
     */
    public double toBase(double amount) {
        return amount * conversionRate;
    }

    /**
     * Converts a value from the base unit into this unit.
     *
     * @param amount value in base unit
     * @return value in this unit
     */
    public double fromBase(double amount) {
        return amount / conversionRate;
    }
}