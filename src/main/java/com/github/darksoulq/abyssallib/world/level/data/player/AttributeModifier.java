package com.github.darksoulq.abyssallib.world.level.data.player;

import java.math.BigDecimal;

/**
 * Represents a modifier applied to an attribute's base value.
 * Supports arithmetic operations (add, subtract, multiply, divide)
 * for numeric types including: Integer, Float, Double, Long, Short, Byte, and BigDecimal.
 *
 * @param <T> The numeric type of the attribute being modified.
 */
public class AttributeModifier<T extends Number> {

    /** The modifier value to apply. */
    private final T value;

    /** The arithmetic operation to apply. */
    private final AttributeOperation operation;

    /**
     * Constructs a new attribute modifier with a given value and operation.
     *
     * @param value     The value to apply as a modifier.
     * @param operation The operation used when applying the modifier.
     */
    public AttributeModifier(T value, AttributeOperation operation) {
        this.value = value;
        this.operation = operation;
    }

    /**
     * @return The raw modifier value.
     */
    public T getValue() {
        return value;
    }

    /**
     * @return The operation that this modifier performs.
     */
    public AttributeOperation getOperation() {
        return operation;
    }

    /**
     * Applies this modifier to an {@code int} base value.
     *
     * @param base The original base value.
     * @return The modified result.
     */
    public int applyToInt(int base) {
        int mod = value.intValue();
        return switch (operation) {
            case ADD -> base + mod;
            case SUBTRACT -> base - mod;
            case MULTIPLY -> base * mod;
            case DIVIDE -> mod == 0 ? base : base / mod;
        };
    }

    /**
     * Applies this modifier to a {@code float} base value.
     *
     * @param base The original base value.
     * @return The modified result.
     */
    public float applyToFloat(float base) {
        float mod = value.floatValue();
        return switch (operation) {
            case ADD -> base + mod;
            case SUBTRACT -> base - mod;
            case MULTIPLY -> base * mod;
            case DIVIDE -> mod == 0.0f ? base : base / mod;
        };
    }

    /**
     * Applies this modifier to a {@code double} base value.
     *
     * @param base The original base value.
     * @return The modified result.
     */
    public double applyToDouble(double base) {
        double mod = value.doubleValue();
        return switch (operation) {
            case ADD -> base + mod;
            case SUBTRACT -> base - mod;
            case MULTIPLY -> base * mod;
            case DIVIDE -> mod == 0.0 ? base : base / mod;
        };
    }

    /**
     * Applies this modifier to a {@code long} base value.
     *
     * @param base The original base value.
     * @return The modified result.
     */
    public long applyToLong(long base) {
        long mod = value.longValue();
        return switch (operation) {
            case ADD -> base + mod;
            case SUBTRACT -> base - mod;
            case MULTIPLY -> base * mod;
            case DIVIDE -> mod == 0L ? base : base / mod;
        };
    }

    /**
     * Applies this modifier to a {@code short} base value.
     *
     * @param base The original base value.
     * @return The modified result.
     */
    public short applyToShort(short base) {
        short mod = value.shortValue();
        return (short) switch (operation) {
            case ADD -> base + mod;
            case SUBTRACT -> base - mod;
            case MULTIPLY -> base * mod;
            case DIVIDE -> mod == 0 ? base : base / mod;
        };
    }

    /**
     * Applies this modifier to a {@code byte} base value.
     *
     * @param base The original base value.
     * @return The modified result.
     */
    public byte applyToByte(byte base) {
        byte mod = value.byteValue();
        return (byte) switch (operation) {
            case ADD -> base + mod;
            case SUBTRACT -> base - mod;
            case MULTIPLY -> base * mod;
            case DIVIDE -> mod == 0 ? base : base / mod;
        };
    }

    /**
     * Applies this modifier to a {@code BigDecimal} base value.
     *
     * @param base The original base value.
     * @return The modified result.
     */
    public BigDecimal applyToBigDecimal(BigDecimal base) {
        BigDecimal mod = (value instanceof BigDecimal b) ? b : new BigDecimal(value.toString());
        return switch (operation) {
            case ADD -> base.add(mod);
            case SUBTRACT -> base.subtract(mod);
            case MULTIPLY -> base.multiply(mod);
            case DIVIDE -> mod.compareTo(BigDecimal.ZERO) == 0 ? base : base.divide(mod, BigDecimal.ROUND_HALF_UP);
        };
    }
}
