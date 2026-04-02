package com.github.darksoulq.abyssallib.common.energy;

/**
 * A node that acts purely as a conduit for energy transfer.
 */
public interface EnergyConductor extends EnergyNode {

    /**
     * @return maximum transfer rate per tick
     */
    double getTransferRate();
}