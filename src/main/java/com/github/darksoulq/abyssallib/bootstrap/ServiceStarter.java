package com.github.darksoulq.abyssallib.bootstrap;

import com.github.darksoulq.abyssallib.common.energy.EnergyNetwork;
import com.github.darksoulq.abyssallib.server.translation.internal.ItemPacketModifier;
import com.github.darksoulq.abyssallib.world.item.internal.ItemTicker;

public final class ServiceStarter {

    public static void init() {
        ItemTicker.start();
        ItemPacketModifier.startUpdater();
        EnergyNetwork.init();
    }
}