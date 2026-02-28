package com.github.darksoulq.abyssallib.world.data.statistic;

import net.kyori.adventure.key.Key;

/**
 * A type-safe container for player statistics and persistent data.
 * This class uses a sealed hierarchy to restrict statistic types to supported
 * primitives (Integer, Float, and Boolean). Each statistic is bound to a unique
 * {@link Key} used for registration, networking, and disk persistence.
 */
public sealed abstract class Statistic
    permits Statistic.IntStatistic, Statistic.FloatStatistic, Statistic.BooleanStatistic {

    /**
     * The unique {@link Key} identifying this specific statistic.
     */
    private final Key id;

    /**
     * Internal constructor for statistic implementations.
     *
     * @param id
     * The unique {@link Key} for the statistic.
     */
    protected Statistic(Key id) {
        this.id = id;
    }

    /**
     * Retrieves the unique Key associated with this statistic.
     *
     * @return
     * The statistic's unique {@link Key}.
     */
    public Key getId() {
        return id;
    }

    /**
     * Retrieves the current value of the statistic as a generic object.
     * Implementations return the appropriate wrapper type (e.g., Integer).
     *
     * @return
     * The boxed value of the statistic.
     */
    public abstract Object getValue();

    /**
     * Sets the value of the statistic after performing type validation.
     *
     * @param value
     * The new value to assign.
     * @throws IllegalArgumentException
     * If the provided object type does not match the implementation's expectations.
     */
    public abstract void setValue(Object value);

    /**
     * Creates a deep copy of this statistic instance.
     *
     * @return
     * A new {@link Statistic} instance with the same ID and current value.
     */
    public abstract Statistic clone();

    /**
     * Creates a new Integer-based statistic instance.
     *
     * @param id
     * The unique {@link Key} for the statistic.
     * @param defaultValue
     * The initial value to assign.
     * @return
     * A new {@link IntStatistic} instance.
     */
    public static IntStatistic of(Key id, int defaultValue) {
        return new IntStatistic(id, defaultValue);
    }

    /**
     * Creates a new Float-based statistic instance.
     *
     * @param id
     * The unique {@link Key} for the statistic.
     * @param defaultValue
     * The initial value to assign.
     * @return
     * A new {@link FloatStatistic} instance.
     */
    public static FloatStatistic of(Key id, float defaultValue) {
        return new FloatStatistic(id, defaultValue);
    }

    /**
     * Creates a new Boolean-based statistic instance.
     *
     * @param id
     * The unique {@link Key} for the statistic.
     * @param defaultValue
     * The initial value to assign.
     * @return
     * A new {@link BooleanStatistic} instance.
     */
    public static BooleanStatistic of(Key id, boolean defaultValue) {
        return new BooleanStatistic(id, defaultValue);
    }

    /**
     * Implementation of {@link Statistic} for integer values.
     */
    public static final class IntStatistic extends Statistic {
        private int value;

        private IntStatistic(Key id, int defaultValue) {
            super(id);
            this.value = defaultValue;
        }

        @Override
        public Integer getValue() {
            return value;
        }

        @Override
        public void setValue(Object value) {
            if (!(value instanceof Integer i)) {
                throw new IllegalArgumentException("Expected Integer for IntStatistic");
            }
            this.value = i;
        }

        @Override
        public IntStatistic clone() {
            return new IntStatistic(getId(), value);
        }
    }

    /**
     * Implementation of {@link Statistic} for floating-point values.
     */
    public static final class FloatStatistic extends Statistic {
        private float value;

        private FloatStatistic(Key id, float defaultValue) {
            super(id);
            this.value = defaultValue;
        }

        @Override
        public Float getValue() {
            return value;
        }

        @Override
        public void setValue(Object value) {
            if (!(value instanceof Float f)) {
                throw new IllegalArgumentException("Expected Float for FloatStatistic");
            }
            this.value = f;
        }

        @Override
        public FloatStatistic clone() {
            return new FloatStatistic(getId(), value);
        }
    }

    /**
     * Implementation of {@link Statistic} for boolean values.
     */
    public static final class BooleanStatistic extends Statistic {
        private boolean value;

        private BooleanStatistic(Key id, boolean defaultValue) {
            super(id);
            this.value = defaultValue;
        }

        @Override
        public Boolean getValue() {
            return value;
        }

        @Override
        public void setValue(Object value) {
            if (!(value instanceof Boolean b)) {
                throw new IllegalArgumentException("Expected Boolean for BooleanStatistic");
            }
            this.value = b;
        }

        @Override
        public BooleanStatistic clone() {
            return new BooleanStatistic(getId(), value);
        }
    }
}