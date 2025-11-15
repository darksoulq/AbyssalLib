package com.github.darksoulq.abyssallib.world.data.statistic;

import com.github.darksoulq.abyssallib.common.util.Identifier;

public abstract class Statistic {
    private final Identifier id;

    protected Statistic(Identifier id) {
        this.id = id;
    }

    public Identifier getId() {
        return id;
    }

    public abstract Object getValue();
    public abstract void setValue(Object value);

    public abstract Statistic clone();

    public static IntStatistic of(Identifier id, int defaultValue) {
        return new IntStatistic(id, defaultValue);
    }
    public static FloatStatistic of(Identifier id, float defaultValue) {
        return new FloatStatistic(id, defaultValue);
    }
    public static BooleanStatistic of(Identifier id, boolean defaultValue) {
        return new BooleanStatistic(id, defaultValue);
    }

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
