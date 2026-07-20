package com.github.darksoulq.abyssallib.world.menu;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public class ContainerData {
    private final int[] data;

    public ContainerData(int size) {
        this.data = new int[size];
    }

    public int get(int index) {
        return index >= 0 && index < this.data.length ? this.data[index] : 0;
    }

    public void set(int index, int value) {
        if (index >= 0 && index < this.data.length) {
            this.data[index] = value;
        }
    }

    public int getCount() {
        return this.data.length;
    }
}