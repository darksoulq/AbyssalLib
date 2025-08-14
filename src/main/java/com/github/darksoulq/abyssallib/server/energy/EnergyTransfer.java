package com.github.darksoulq.abyssallib.server.energy;

import com.github.darksoulq.abyssallib.server.energy.event.EnergyTransferEvent;
import org.bukkit.Bukkit;

public final class EnergyTransfer {
    private EnergyTransfer() {}

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
        Bukkit.getPluginManager().callEvent(event);
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
