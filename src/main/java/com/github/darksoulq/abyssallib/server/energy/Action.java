package com.github.darksoulq.abyssallib.server.energy;

public enum Action {
    EXECUTE, SIMULATE;
    public boolean execute() { return this == EXECUTE; }
    public boolean simulate() { return this == SIMULATE; }
}
