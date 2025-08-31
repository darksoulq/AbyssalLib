package com.github.darksoulq.abyssallib.common.energy.impl;

import com.github.darksoulq.abyssallib.common.energy.Action;
import com.github.darksoulq.abyssallib.common.energy.EnergyContainer;
import com.github.darksoulq.abyssallib.common.energy.Resizable;
import com.github.darksoulq.abyssallib.common.energy.event.EnergyChangeEvent;
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.RecordCodecBuilder;
import com.github.darksoulq.abyssallib.server.event.EventBus;

public final class SimpleEnergyContainer implements EnergyContainer, Resizable {
    public static final Codec<SimpleEnergyContainer> CODEC = RecordCodecBuilder.create(
            Codecs.DOUBLE.fieldOf("energy", SimpleEnergyContainer::getEnergy),
            Codecs.DOUBLE.fieldOf("capacity", SimpleEnergyContainer::getCapacity),
            SimpleEnergyContainer::new
    );
    private final AtomicDouble energy;
    private final AtomicDouble capacity;

    public SimpleEnergyContainer(double capacity) {
        this(capacity, 0.0);
    }

    public SimpleEnergyContainer(double capacity, double initial) {
        this.capacity = new AtomicDouble(Math.max(0.0, capacity));
        double init = Math.max(0.0, Math.min(initial, this.capacity.get()));
        this.energy = new AtomicDouble(init);
    }

    @Override
    public double getEnergy() {
        return energy.get();
    }
    @Override
    public double getCapacity() {
        return capacity.get();
    }

    @Override
    public double insert(double amount, Action action) {
        if (amount <= 0.0) return 0.0;
        double cap = capacity.get();
        if (cap <= 0.0) return 0.0;
        boolean simulate = action.simulate();
        if (simulate) {
            double space = cap - energy.get();
            return Math.max(0.0, Math.min(space, amount));
        }
        while (true) {
            double cur = energy.get();
            double space = cap - cur;
            if (space <= 0.0) return 0.0;
            double toInsert = Math.min(space, amount);
            double next = cur + toInsert;
            if (energy.compareAndSet(cur, next)) {
                EventBus.post(new EnergyChangeEvent(this, cur, next));
                return toInsert;
            }
        }
    }

    @Override
    public double extract(double amount, Action action) {
        if (amount <= 0.0) return 0.0;
        boolean simulate = action.simulate();
        if (simulate) {
            double cur = energy.get();
            return Math.min(cur, amount);
        }
        while (true) {
            double cur = energy.get();
            if (cur <= 0.0) return 0.0;
            double toTake = Math.min(cur, amount);
            double next = cur - toTake;
            if (energy.compareAndSet(cur, next)) {
                EventBus.post(new EnergyChangeEvent(this, cur, next));
                return toTake;
            }
        }
    }

    @Override
    public void setCapacity(double cap) {
        double newCap = Math.max(0.0, cap);
        capacity.set(newCap);
        while (true) {
            double prev = energy.get();
            if (prev <= newCap) break;
            double nxt = Math.min(prev, newCap);
            if (energy.compareAndSet(prev, nxt)) {
                EventBus.post(new EnergyChangeEvent(this, prev, nxt));
                break;
            }
        }
    }
}