package com.github.darksoulq.abyssallib.common.energy;

import com.github.darksoulq.abyssallib.server.event.EventBus;
import com.github.darksoulq.abyssallib.server.event.custom.energy.EnergyNodeChangeEvent;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Base implementation of {@link EnergyConductor} representing a transfer-only node
 * with no persistent storage beyond a single-tick buffer.
 *
 * <p>This implementation enforces a strict {@link #transferRate} which limits both
 * insertion and extraction per operation. Unlike standard nodes, conductors act
 * as transient carriers within the network.</p>
 *
 * <p>This class does not automatically register itself into {@link EnergyNetwork}.</p>
 */
public abstract class AbstractEnergyConductor implements EnergyConductor {

    /**
     * Mapping of connected neighboring nodes indexed by their relative {@link BlockFace}.
     */
    private final Map<BlockFace, EnergyNode> connections = new ConcurrentHashMap<>();

    /**
     * The energy unit used for all transfer operations.
     */
    private final EnergyUnit unit;

    /**
     * The maximum transferable energy per operation.
     */
    private final double transferRate;

    /**
     * Temporary buffer storing energy during a single tick.
     */
    private double buffer;

    /**
     * Creates a new conductor with a defined transfer rate and unit.
     *
     * @param transferRate maximum energy transferable per operation
     * @param unit         energy unit used for this conductor
     */
    public AbstractEnergyConductor(double transferRate, EnergyUnit unit) {
        this.transferRate = Math.max(0, transferRate);
        this.unit = unit;
        this.buffer = 0;
    }

    /**
     * Creates a new conductor using the default unit {@link EnergyUnits#PE}.
     *
     * @param transferRate maximum energy transferable per operation
     */
    public AbstractEnergyConductor(double transferRate) {
        this(transferRate, EnergyUnits.PE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getTransferRate() {
        return transferRate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EnergyUnit getUnit() {
        return unit;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<BlockFace, EnergyNode> getConnections() {
        return connections;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getEnergy() {
        return buffer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getCapacity() {
        return transferRate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getMaxInsert() {
        return transferRate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getMaxExtract() {
        return transferRate;
    }

    /**
     * Attempts to insert energy into this conductor buffer.
     *
     * @param side   the side the energy is inserted from, or {@code null}
     * @param amount the amount of energy to insert
     * @param action whether to execute or simulate
     * @return the amount of energy accepted into the buffer
     */
    @Override
    public double insert(@Nullable BlockFace side, double amount, Action action) {
        if (amount <= 0 || transferRate <= 0) return 0;

        double toInsert = Math.min(amount, transferRate - buffer);
        if (toInsert <= 0) return 0;

        if (action == Action.EXECUTE) {
            double old = buffer;
            buffer += toInsert;
            EventBus.post(new EnergyNodeChangeEvent(this, unit, old, buffer, !org.bukkit.Bukkit.isPrimaryThread()));
            EnergyNetwork.markActive(this);
        }

        return toInsert;
    }

    /**
     * Attempts to extract energy from this conductor buffer.
     *
     * @param side   the side the energy is extracted from, or {@code null}
     * @param amount the amount of energy to extract
     * @param action whether to execute or simulate
     * @return the amount of energy extracted from the buffer
     */
    @Override
    public double extract(@Nullable BlockFace side, double amount, Action action) {
        if (amount <= 0 || buffer <= 0) return 0;

        double toExtract = Math.min(amount, buffer);
        if (toExtract <= 0) return 0;

        if (action == Action.EXECUTE) {
            double old = buffer;
            buffer -= toExtract;
            EventBus.post(new EnergyNodeChangeEvent(this, unit, old, buffer, !org.bukkit.Bukkit.isPrimaryThread()));
        }

        return toExtract;
    }
}