package com.github.darksoulq.abyssallib.world.gen.feature.tree;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.registry.DeferredRegistry;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.gen.feature.tree.root.MangroveRootPlacer;
import com.github.darksoulq.abyssallib.world.gen.feature.tree.root.RootPlacerType;
import com.github.darksoulq.abyssallib.world.gen.feature.tree.root.SpreadingRootPlacer;

public class RootPlacers {
    public static final DeferredRegistry<RootPlacerType<?>> ROOT_PLACERS = DeferredRegistry.create(Registries.ROOT_PLACERS, AbyssalLib.PLUGIN_ID);

    public static final RootPlacerType<?> MANGROVE = ROOT_PLACERS.register("mangrove", id -> MangroveRootPlacer.TYPE);
    public static final RootPlacerType<?> SPREADING = ROOT_PLACERS.register("spreading", id -> SpreadingRootPlacer.TYPE);
}