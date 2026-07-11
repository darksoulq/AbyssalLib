package com.github.darksoulq.abyssallib.world.menu;

public abstract class DataSlot {
    private int prevValue;

    public static DataSlot shared(final ContainerData data, final int index) {
        return new DataSlot() {
            @Override
            public int get() {
                return data.get(index);
            }

            @Override
            public void set(int value) {
                data.set(index, value);
            }
        };
    }

    public static DataSlot standalone() {
        return new DataSlot() {
            private int value;

            @Override
            public int get() {
                return this.value;
            }

            @Override
            public void set(int value) {
                this.value = value;
            }
        };
    }

    public abstract int get();

    public abstract void set(int value);

    public boolean checkAndClearUpdateFlag() {
        int current = this.get();
        boolean changed = current != this.prevValue;
        this.prevValue = current;
        return changed;
    }

    public static class Boolean extends DataSlot {
        private boolean value;

        public Boolean(boolean initial) {
            this.value = initial;
        }

        @Override
        public int get() {
            return this.value ? 1 : 0;
        }

        @Override
        public void set(int value) {
            this.value = (value != 0);
        }

        public boolean getBoolean() {
            return this.value;
        }

        public void setBoolean(boolean value) {
            this.value = value;
        }
    }

    public static class Float extends DataSlot {
        private float value;

        public Float(float initial) {
            this.value = initial;
        }

        @Override
        public int get() {
            return java.lang.Float.floatToIntBits(this.value);
        }

        @Override
        public void set(int value) {
            this.value = java.lang.Float.intBitsToFloat(value);
        }

        public float getFloat() {
            return this.value;
        }

        public void setFloat(float value) {
            this.value = value;
        }
    }

    public static class Enum<E extends java.lang.Enum<E>> extends DataSlot {
        private final Class<E> enumClass;
        private E value;

        public Enum(Class<E> enumClass, E initial) {
            this.enumClass = enumClass;
            this.value = initial;
        }

        @Override
        public int get() {
            return this.value.ordinal();
        }

        @Override
        public void set(int value) {
            E[] constants = this.enumClass.getEnumConstants();
            this.value = (value >= 0 && value < constants.length) ? constants[value] : constants[0];
        }

        public E getEnum() {
            return this.value;
        }

        public void setEnum(E value) {
            this.value = value;
        }
    }
}