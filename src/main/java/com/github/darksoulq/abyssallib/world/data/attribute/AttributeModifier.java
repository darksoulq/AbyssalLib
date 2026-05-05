package com.github.darksoulq.abyssallib.world.data.attribute;

import net.kyori.adventure.key.Key;
import org.bukkit.attribute.AttributeModifier.Operation;

/**
 * Represents a modifier applied to an attribute's base value.
 */
public class AttributeModifier {
    /** The unique identifier for this specific modifier. */
    private final Key key;
    /** The numeric value to apply as a modifier. */
    private final double amount;
    /** The arithmetic operation logic to apply to the base value. */
    private final Operation operation;

    /**
     * Constructs a new attribute modifier.
     *
     * @param key       The unique key identifying this modifier.
     * @param amount    The numeric value to apply.
     * @param operation The {@link Operation} defining how the value interacts with the base.
     */
    public AttributeModifier(Key key, double amount, Operation operation) {
        this.key = key;
        this.amount = amount;
        this.operation = operation;
    }

    /**
     * Retrieves the unique identifier of this modifier.
     *
     * @return The modifier key.
     */
    public Key getKey() {
        return key;
    }

    /**
     * Retrieves the raw numeric value associated with this modifier.
     *
     * @return The modifier amount.
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Retrieves the specific arithmetic operation performed by this modifier.
     *
     * @return The {@link Operation} representing the calculation type.
     */
    public Operation getOperation() {
        return operation;
    }
}