package com.github.darksoulq.abyssallib.world.data.statistic;

import net.kyori.adventure.key.Key;

/**
 * A type-safe container for player statistics.
 * This class uses a sealed hierarchy to restrict statistic types to supported
 * primitives (Integer, Float, and Boolean). It utilizes generics to ensure
 * compile-time type safety for value manipulation.
 *
 * @param <T>
 * The boxed numeric or boolean type handled by this statistic.
 */
public sealed abstract class Statistic<T>
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
     * Retrieves the current value of the statistic.
     *
     * @return
     * The current value of type {@code T}.
     */
    public abstract T getValue();

    /**
     * Sets a new value for the statistic.
     *
     * @param value
     * The new value to be stored.
     */
    public abstract void setValue(T value);

    /**
     * Creates a deep copy of this statistic instance.
     *
     * @return
     * A new {@link Statistic} instance with identical properties.
     */
    public abstract Statistic<T> clone();

    /**
     * Creates a new Integer-based statistic instance.
     *
     * @param id
     * The unique {@link Key} for the statistic.
     * @param defaultValue
     * The initial value.
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
     * The initial value.
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
     * The initial value.
     * @return
     * A new {@link BooleanStatistic} instance.
     */
    public static BooleanStatistic of(Key id, boolean defaultValue) {
        return new BooleanStatistic(id, defaultValue);
    }

    /**
     * Implementation of {@link Statistic} for integer values.
     */
    public static final class IntStatistic extends Statistic<Integer> {
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
        public void setValue(Integer value) {
            this.value = value;
        }

        @Override
        public IntStatistic clone() {
            return new IntStatistic(getId(), value);
        }
    }

    /**
     * Implementation of {@link Statistic} for floating-point values.
     */
    public static final class FloatStatistic extends Statistic<Float> {
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
        public void setValue(Float value) {
            this.value = value;
        }

        @Override
        public FloatStatistic clone() {
            return new FloatStatistic(getId(), value);
        }
    }

    /**
     * Implementation of {@link Statistic} for boolean values.
     */
    public static final class BooleanStatistic extends Statistic<Boolean> {
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
        public void setValue(Boolean value) {
            this.value = value;
        }

        @Override
        public BooleanStatistic clone() {
            return new BooleanStatistic(getId(), value);
        }
    }
}