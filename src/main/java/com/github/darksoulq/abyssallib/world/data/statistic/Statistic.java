package com.github.darksoulq.abyssallib.world.data.statistic;

import com.github.darksoulq.abyssallib.common.util.Identifier;

/**
 * A type-safe container for player statistics.
 * <p>
 * This class uses a sealed hierarchy to restrict statistic types to supported
 * primitives (Int, Float, Boolean). Each statistic is bound to a unique
 * {@link Identifier} used for registration and persistence.
 */
public sealed abstract class Statistic
    permits Statistic.IntStatistic, Statistic.FloatStatistic, Statistic.BooleanStatistic {

    /** The unique identifier for this statistic. */
    private final Identifier id;

    /**
     * Internal constructor for statistic implementations.
     *
     * @param id the unique identifier
     */
    protected Statistic(Identifier id) {
        this.id = id;
    }

    /**
     * Gets the unique identifier of this statistic.
     *
     * @return the identifier
     */
    public Identifier getId() {
        return id;
    }

    /**
     * Gets the current value of the statistic as a generic object.
     *
     * @return the value
     */
    public abstract Object getValue();

    /**
     * Sets the value of the statistic.
     *
     * @param value the new value
     * @throws IllegalArgumentException if the object type does not match the implementation
     */
    public abstract void setValue(Object value);

    /**
     * Creates a deep copy of this statistic.
     *
     * @return a new statistic instance with the same ID and value
     */
    public abstract Statistic clone();

    /**
     * Creates a new Integer-based statistic.
     *
     * @param id           the unique identifier
     * @param defaultValue the starting value
     * @return a new IntStatistic instance
     */
    public static IntStatistic of(Identifier id, int defaultValue) {
        return new IntStatistic(id, defaultValue);
    }

    /**
     * Creates a new Float-based statistic.
     *
     * @param id           the unique identifier
     * @param defaultValue the starting value
     * @return a new FloatStatistic instance
     */
    public static FloatStatistic of(Identifier id, float defaultValue) {
        return new FloatStatistic(id, defaultValue);
    }

    /**
     * Creates a new Boolean-based statistic.
     *
     * @param id           the unique identifier
     * @param defaultValue the starting value
     * @return a new BooleanStatistic instance
     */
    public static BooleanStatistic of(Identifier id, boolean defaultValue) {
        return new BooleanStatistic(id, defaultValue);
    }

    /**
     * Implementation of {@link Statistic} for integer values.
     */
    public static final class IntStatistic extends Statistic {
        private int value;

        private IntStatistic(Identifier id, int defaultValue) {
            super(id);
            this.value = defaultValue;
        }

        @Override
        public Integer getValue() {
            return value;
        }

        @Override
        public void setValue(Object value) {
            if (!(value instanceof Integer i)) throw new IllegalArgumentException("Expected Integer");
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

        private FloatStatistic(Identifier id, float defaultValue) {
            super(id);
            this.value = defaultValue;
        }

        @Override
        public Float getValue() {
            return value;
        }

        @Override
        public void setValue(Object value) {
            if (!(value instanceof Float f)) throw new IllegalArgumentException("Expected Float");
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

        private BooleanStatistic(Identifier id, boolean defaultValue) {
            super(id);
            this.value = defaultValue;
        }

        @Override
        public Boolean getValue() {
            return value;
        }

        @Override
        public void setValue(Object value) {
            if (!(value instanceof Boolean b)) throw new IllegalArgumentException("Expected Boolean");
            this.value = b;
        }

        @Override
        public BooleanStatistic clone() {
            return new BooleanStatistic(getId(), value);
        }
    }
}