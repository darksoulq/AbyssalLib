package com.github.darksoulq.abyssallib.world.entity.data;

import java.math.BigDecimal;

/**
 * Represents a modifier applied to an attribute's base value.
 * This class supports arithmetic operations including addition, subtraction,
 * multiplication, and division for a wide variety of numeric types.
 *
 * @param <T>
 * The numeric type of the attribute being modified, extending {@link Number}.
 */
public class AttributeModifier<T extends Number> {

    /**
     * The modifier value to apply during calculations.
     */
    private final T value;

    /**
     * The arithmetic operation logic to apply to the base value.
     */
    private final AttributeOperation operation;

    /**
     * Constructs a new attribute modifier with a specific value and operation type.
     *
     * @param value
     * The numeric value to apply as a modifier.
     * @param operation
     * The {@link AttributeOperation} defining how the value interacts with the base.
     */
    public AttributeModifier(T value, AttributeOperation operation) {
        this.value = value;
        this.operation = operation;
    }

    /**
     * Retrieves the raw numeric value associated with this modifier.
     *
     * @return
     * The modifier value of type {@code T}.
     */
    public T getValue() {
        return value;
    }

    /**
     * Retrieves the specific arithmetic operation performed by this modifier.
     *
     * @return
     * The {@link AttributeOperation} constant representing the operation.
     */
    public AttributeOperation getOperation() {
        return operation;
    }

    /**
     * Applies this modifier to an integer base value.
     *
     * @param base
     * The original base value to be modified.
     * @return
     * The resulting modified integer value.
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
     * Applies this modifier to a floating-point base value.
     *
     * @param base
     * The original base value to be modified.
     * @return
     * The resulting modified float value.
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
     * Applies this modifier to a double-precision base value.
     *
     * @param base
     * The original base value to be modified.
     * @return
     * The resulting modified double value.
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
     * Applies this modifier to a long integer base value.
     *
     * @param base
     * The original base value to be modified.
     * @return
     * The resulting modified long value.
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
     * Applies this modifier to a short integer base value.
     *
     * @param base
     * The original base value to be modified.
     * @return
     * The resulting modified short value.
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
     * Applies this modifier to a byte base value.
     *
     * @param base
     * The original base value to be modified.
     * @return
     * The resulting modified byte value.
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
     * Applies this modifier to a {@link BigDecimal} base value for high-precision arithmetic.
     *
     * @param base
     * The original base value to be modified.
     * @return
     * The resulting modified BigDecimal value.
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