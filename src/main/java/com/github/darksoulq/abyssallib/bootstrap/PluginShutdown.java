package com.github.darksoulq.abyssallib.bootstrap;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.energy.EnergyNetwork;
import com.github.darksoulq.abyssallib.world.block.internal.BlockManager;
import com.github.darksoulq.abyssallib.world.multiblock.internal.MultiblockManager;

public final class PluginShutdown {

    public static void execute() {
        BlockManager.save();
        MultiblockManager.save();
        EnergyNetwork.save();

        if (AbyssalLib.PACK_SERVER != null && AbyssalLib.PACK_SERVER.isEnabled()) {
            AbyssalLib.PACK_SERVER.stop();
        }

        if (AbyssalLib.PERMISSION_MANAGER != null) {
            AbyssalLib.PERMISSION_MANAGER.shutdown();
        }

        if (AbyssalLib.PERMISSION_WEB_SERVER != null && AbyssalLib.PERMISSION_WEB_SERVER.isEnabled()) {
            AbyssalLib.PERMISSION_WEB_SERVER.stop();
        }
    }
}