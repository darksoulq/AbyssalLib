package com.github.darksoulq.abyssallib.bootstrap;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.energy.EnergyNetwork;
import com.github.darksoulq.abyssallib.server.translation.internal.ItemPacketModifier;
import com.github.darksoulq.abyssallib.world.item.internal.ItemTicker;

public final class Services {
    public static void init() {
        if (AbyssalLib.CONFIG.features.enableItemTicking.get()) ItemTicker.start();
        if (AbyssalLib.CONFIG.features.tickServerTranslations.get()) ItemPacketModifier.startUpdater();
        if (AbyssalLib.CONFIG.features.enableEnergyNetwork.get()) EnergyNetwork.init();
    }
}