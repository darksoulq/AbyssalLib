package com.github.darksoulq.abyssallib.world.level.data.player;

import com.github.darksoulq.abyssallib.world.level.data.Identifier;

import java.math.BigDecimal;
import java.util.*;

/**
 * Represents a player attribute with a specific type and default value,
 *
 * @param <T> The type of the attribute value (e.g. Integer, Float, Double, etc).
 */
public final class Attribute<T extends Number> {

    /** The unique key identifying this attribute in player data. */
    private final String key;

    /** The Java class of the attribute's value type. */
    private final Class<T> type;

    /** The default value used if no data is stored for this attribute. */
    private final T defaultValue;

    /** Map of active modifiers applied to this attribute, indexed by a unique identifier. */
    private final Map<Identifier, AttributeModifier<T>> modifiers = new LinkedHashMap<>();

    /**
     * Constructs a new attribute definition.
     *
     * @param key          The unique key for this attribute.
     * @param type         The type class (e.g. Integer.class).
     * @param defaultValue The fallback value when not set.
     */
    public Attribute(String key, Class<T> type, T defaultValue) {
        this.key = key;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    /**
     * Returns the unique key used to identify this attribute.
     *
     * @return the attribute key
     */
    public String key() {
        return key;
    }

    /**
     * Returns the Java class representing the type of this attribute.
     *
     * @return the type class
     */
    public Class<T> type() {
        return type;
    }

    /**
     * Returns the default value of this attribute.
     *
     * @return the default value
     */
    public T defaultValue() {
        return defaultValue;
    }

    /**
     * Adds a numeric modifier to this attribute.
     * Has no effect if the type is not a supported number type.
     *
     * @param id        The identifier for this modifier.
     * @param modifier  The modifier value.
     * @param operation The arithmetic operation to apply.
     */
    public void addModifier(Identifier id, T modifier, AttributeOperation operation) {
        if (!isNumericType()) return;
        modifiers.put(id, new AttributeModifier<>(modifier, operation));
    }

    /**
     * Removes a modifier from this attribute using its identifier.
     *
     * @param id The identifier of the modifier to remove.
     */
    public void removeModifier(Identifier id) {
        modifiers.remove(id);
    }

    /**
     * Returns the map of identifier:attribute modifier
     *
     * @return The attribute modifier map
     */
    public Map<Identifier, AttributeModifier<T>> getModifiers() {
        return modifiers;
    }

    /**
     * Applies all active modifiers to a given base value.
     * If no modifiers exist or type is unsupported, the base is returned unmodified.
     *
     * @param base The base (unmodified) value of the attribute.
     * @return The final modified value.
     */
    @SuppressWarnings("unchecked")
    public T applyModifiers(T base) {
        if (modifiers.isEmpty() || base == null) return base;

        if (type == Integer.class) {
            int value = (Integer) base;
            for (AttributeModifier<T> mod : modifiers.values()) {
                value = mod.applyToInt(value);
            }
            return (T) Integer.valueOf(value);
        }

        if (type == Float.class) {
            float value = (Float) base;
            for (AttributeModifier<T> mod : modifiers.values()) {
                value = mod.applyToFloat(value);
            }
            return (T) Float.valueOf(value);
        }

        if (type == Double.class) {
            double value = (Double) base;
            for (AttributeModifier<T> mod : modifiers.values()) {
                value = mod.applyToDouble(value);
            }
            return (T) Double.valueOf(value);
        }

        if (type == Long.class) {
            long value = (Long) base;
            for (AttributeModifier<T> mod : modifiers.values()) {
                value = mod.applyToLong(value);
            }
            return (T) Long.valueOf(value);
        }

        if (type == Short.class) {
            short value = (Short) base;
            for (AttributeModifier<T> mod : modifiers.values()) {
                value = mod.applyToShort(value);
            }
            return (T) Short.valueOf(value);
        }

        if (type == Byte.class) {
            byte value = (Byte) base;
            for (AttributeModifier<T> mod : modifiers.values()) {
                value = mod.applyToByte(value);
            }
            return (T) Byte.valueOf(value);
        }

        if (type == BigDecimal.class) {
            BigDecimal value = (BigDecimal) base;
            for (AttributeModifier<T> mod : modifiers.values()) {
                value = mod.applyToBigDecimal(value);
            }
            return (T) value;
        }
        return base;
    }

    /**
     * Checks if this attribute's type is numeric and supports modifiers.
     *
     * @return true if this is a supported numeric type.
     */
    private boolean isNumericType() {
        return type == Integer.class || type == Float.class || type == Double.class ||
                type == Long.class || type == Short.class || type == Byte.class ||
                type == BigDecimal.class;
    }
}

