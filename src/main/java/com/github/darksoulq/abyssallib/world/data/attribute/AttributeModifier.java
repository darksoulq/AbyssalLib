package com.github.darksoulq.abyssallib.world.data.attribute;

import net.kyori.adventure.key.Key;
import org.bukkit.attribute.AttributeModifier.Operation;

/**
 * Represents a modifier applied to an attribute's base value.
 *
 * @param key       The unique identifier for this specific modifier.
 * @param amount    The numeric value to apply as a modifier.
 * @param operation The arithmetic operation logic to apply to the base value.
 */
public record AttributeModifier(Key key, double amount, Operation operation) {
    /**
     * Constructs a new attribute modifier.
     *
     * @param key       The unique key identifying this modifier.
     * @param amount    The numeric value to apply.
     * @param operation The {@link Operation} defining how the value interacts with the base.
     */
    public AttributeModifier {
    }

    /**
     * Retrieves the unique identifier of this modifier.
     *
     * @return The modifier key.
     */
    @Override
    public Key key() {
        return key;
    }

    /**
     * Retrieves the raw numeric value associated with this modifier.
     *
     * @return The modifier amount.
     */
    @Override
    public double amount() {
        return amount;
    }

    /**
     * Retrieves the specific arithmetic operation performed by this modifier.
     *
     * @return The {@link Operation} representing the calculation type.
     */
    @Override
    public Operation operation() {
        return operation;
    }
}