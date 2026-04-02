package com.github.darksoulq.abyssallib.common.energy;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.registry.DeferredRegistry;
import com.github.darksoulq.abyssallib.server.registry.Registries;

public class EnergyUnits {
    public static final DeferredRegistry<EnergyUnit> ENERGY_UNITS = DeferredRegistry.create(Registries.ENERGY_UNITS, AbyssalLib.PLUGIN_ID);

    public static final EnergyUnit PE = ENERGY_UNITS.register("pe", id -> new EnergyUnit(id, "PE", 1.0));
}