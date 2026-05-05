package com.github.darksoulq.abyssallib.world.data.attribute;

import net.kyori.adventure.key.Key;
import org.bukkit.attribute.AttributeModifier.Operation;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents an active instance of an attribute on a specific entity.
 * Manages the base value and all applied modifiers to compute the final value.
 */
public class AttributeInstance {
    /** The attribute definition this instance represents. */
    private final Attribute attribute;
    /** The current un-modified base value. */
    private double baseValue;
    /** Map of active modifiers applied to this instance, indexed by their unique key. */
    private final Map<Key, AttributeModifier> modifiers = new LinkedHashMap<>();

    /**
     * Constructs a new attribute instance for an entity.
     *
     * @param attribute The attribute definition.
     * @param baseValue The initial base value.
     */
    public AttributeInstance(Attribute attribute, double baseValue) {
        this.attribute = attribute;
        this.baseValue = baseValue;
    }

    /**
     * Retrieves the attribute definition associated with this instance.
     *
     * @return The {@link Attribute}.
     */
    public Attribute getAttribute() {
        return attribute;
    }

    /**
     * Retrieves the raw base value without applying any modifiers.
     *
     * @return The base value.
     */
    public double getBaseValue() {
        return baseValue;
    }

    /**
     * Sets the un-modified base value of this attribute instance.
     *
     * @param baseValue The new base value.
     */
    public void setBaseValue(double baseValue) {
        this.baseValue = baseValue;
    }

    /**
     * Adds a modifier to this attribute instance.
     *
     * @param modifier The {@link AttributeModifier} to add.
     */
    public void addModifier(AttributeModifier modifier) {
        modifiers.put(modifier.getKey(), modifier);
    }

    /**
     * Removes a specific modifier from this attribute instance using its key.
     *
     * @param key The unique {@link Key} of the modifier to remove.
     */
    public void removeModifier(Key key) {
        modifiers.remove(key);
    }

    /**
     * Retrieves a specific modifier applied to this instance.
     *
     * @param key The unique {@link Key} of the modifier.
     * @return The {@link AttributeModifier}, or null if not found.
     */
    public AttributeModifier getModifier(Key key) {
        return modifiers.get(key);
    }

    /**
     * Retrieves all active modifiers on this instance.
     *
     * @return A collection of {@link AttributeModifier}s.
     */
    public Collection<AttributeModifier> getModifiers() {
        return modifiers.values();
    }

    /**
     * Calculates and returns the final value of this attribute after applying all modifiers.
     *
     * @return The final computed value.
     */
    public double getValue() {
        double value = baseValue;

        for (AttributeModifier mod : modifiers.values()) {
            if (mod.getOperation() == Operation.ADD_NUMBER) {
                value += mod.getAmount();
            }
        }

        double addedBase = value;

        for (AttributeModifier mod : modifiers.values()) {
            if (mod.getOperation() == Operation.ADD_SCALAR) {
                addedBase += value * mod.getAmount();
            }
        }
        value = addedBase;

        for (AttributeModifier mod : modifiers.values()) {
            if (mod.getOperation() == Operation.MULTIPLY_SCALAR_1) {
                value += value * mod.getAmount();
            }
        }

        return value;
    }
}