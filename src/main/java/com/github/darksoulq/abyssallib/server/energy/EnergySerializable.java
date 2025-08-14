package com.github.darksoulq.abyssallib.server.energy;

public interface EnergySerializable {
    byte[] serialize();
    void deserialize(byte[] data);
}
