package com.github.darksoulq.abyssallib.common.energy;

import com.github.darksoulq.abyssallib.server.event.EventBus;
import com.github.darksoulq.abyssallib.server.event.custom.energy.EnergyTransferEvent;
import org.bukkit.Bukkit;

/**
 * Utility class for transferring energy between {@link EnergyMutator} instances.
 * Handles event firing and supports simulation mode.
 */
@Deprecated(forRemoval = true)
public final class EnergyTransfer {
    private EnergyTransfer() {
    }

    /**
     * Transfers energy from one container to another.
     * <p>
     * This method:
     * <ul>
     *     <li>Respects the maximum amount specified by {@code max}</li>
     *     <li>Respects available energy in the source and available space in the target</li>
     *     <li>Fires an {@link EnergyTransferEvent} which can modify or cancel the transfer</li>
     *     <li>Supports {@link Action#SIMULATE} mode where no energy is actually moved</li>
     *     <li>Returns the actual amount that would be moved or was moved</li>
     * </ul>
     *
     * @param from   the source energy container
     * @param into   the target energy container
     * @param max    the maximum energy to transfer
     * @param action whether to simulate or execute the transfer
     * @return the amount of energy actually transferred (or would be transferred if simulating)
     */
    public static double move(EnergyMutator from, EnergyMutator into, double max, Action action) {
        if (max <= 0.0) return 0.0;

        double available = from.getEnergy();
        if (available <= 0.0) return 0.0;

        double space = into.getSpace();
        if (space <= 0.0) return 0.0;

        double toMove = Math.min(Math.min(max, available), space);
        if (toMove <= 0.0) return 0.0;

        boolean async = !Bukkit.isPrimaryThread();

        EnergyTransferEvent event = new EnergyTransferEvent(
                (from instanceof EnergyContainer c1 ? c1 : null),
                (into instanceof EnergyContainer c2 ? c2 : null),
                toMove,
                action == Action.SIMULATE,
                async
        );
        EventBus.post(event);

        if (event.isCancelled()) return 0.0;

        toMove = event.getAmount();
        if (toMove <= 0.0) return 0.0;

        if (action == Action.SIMULATE) return toMove;

        double drained = from.extract(toMove, Action.EXECUTE);
        if (drained <= 0.0) return 0.0;

        double filled = into.insert(drained, Action.EXECUTE);

        if (filled < drained) {
            double diff = drained - filled;
            if (diff > 0.0) from.insert(diff, Action.EXECUTE);
        }

        return Math.min(drained, filled);
    }
}
