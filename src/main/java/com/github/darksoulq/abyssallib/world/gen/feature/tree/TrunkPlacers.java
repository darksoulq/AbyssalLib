package com.github.darksoulq.abyssallib.world.gen.feature.tree;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.registry.DeferredRegistry;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.gen.feature.tree.trunk.*;

public class TrunkPlacers {

    public static final DeferredRegistry<TrunkPlacerType<?>> TRUNK_PLACERS = DeferredRegistry.create(Registries.TRUNK_PLACERS, AbyssalLib.PLUGIN_ID);

    public static final TrunkPlacerType<?> STRAIGHT = TRUNK_PLACERS.register("straight", id -> StraightTrunkPlacer.TYPE);
    public static final TrunkPlacerType<?> FORKING = TRUNK_PLACERS.register("forking", id -> ForkingTrunkPlacer.TYPE);
    public static final TrunkPlacerType<?> GIANT = TRUNK_PLACERS.register("giant", id -> GiantTrunkPlacer.TYPE);
    public static final TrunkPlacerType<?> BENDING = TRUNK_PLACERS.register("bending", id -> BendingTrunkPlacer.TYPE);
    public static final TrunkPlacerType<?> UPWARD_BRANCHING = TRUNK_PLACERS.register("upward_branching", id -> UpwardBranchingTrunkPlacer.TYPE);
}