package com.github.darksoulq.abyssallib.common.util;

public interface IntSequence {
    int[] get();

    static IntSequence range(int min, int max, int step) {
        return () -> {
            int size = Math.max(0, (max - min) / step + 1);
            int[] arr = new int[size];
            for (int i = 0; i < size; i++) {
                arr[i] = min + (i * step);
            }
            return arr;
        };
    }

    static IntSequence values(int... values) {
        return () -> values;
    }

    static IntSequence one(int value) {
        return () -> new int[]{value};
    }
}