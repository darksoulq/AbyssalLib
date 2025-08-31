package com.github.darksoulq.abyssallib.common.energy.impl;

import java.util.concurrent.atomic.AtomicLong;

public final class AtomicDouble {
    private final AtomicLong bits;
    public AtomicDouble(double initial) { bits = new AtomicLong(Double.doubleToRawLongBits(initial)); }
    public final double get() { return Double.longBitsToDouble(bits.get()); }
    public final void set(double value) { bits.set(Double.doubleToRawLongBits(value)); }
    public final boolean compareAndSet(double expect, double update) {
        return bits.compareAndSet(Double.doubleToRawLongBits(expect), Double.doubleToRawLongBits(update));
    }
    public final double getAndAdd(double delta) {
        while (true) {
            long cur = bits.get();
            double curVal = Double.longBitsToDouble(cur);
            double nextVal = curVal + delta;
            long next = Double.doubleToRawLongBits(nextVal);
            if (bits.compareAndSet(cur, next)) return curVal;
        }
    }
    public final double addAndGet(double delta) {
        while (true) {
            long cur = bits.get();
            double curVal = Double.longBitsToDouble(cur);
            double nextVal = curVal + delta;
            long next = Double.doubleToRawLongBits(nextVal);
            if (bits.compareAndSet(cur, next)) return nextVal;
        }
    }
    public final double getAndSet(double newValue) {
        long next = Double.doubleToRawLongBits(newValue);
        long prev = bits.getAndSet(next);
        return Double.longBitsToDouble(prev);
    }
}
