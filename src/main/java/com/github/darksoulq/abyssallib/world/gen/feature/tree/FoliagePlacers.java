package com.github.darksoulq.abyssallib.world.gen.feature.tree;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.registry.DeferredRegistry;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.gen.feature.tree.foliage.*;

public class FoliagePlacers {
    public static final DeferredRegistry<FoliagePlacerType<?>> FOLIAGE_PLACERS = DeferredRegistry.create(Registries.FOLIAGE_PLACERS, AbyssalLib.PLUGIN_ID);

    public static final FoliagePlacerType<?> BLOB = FOLIAGE_PLACERS.register("blob", id -> BlobFoliagePlacer.TYPE);
    public static final FoliagePlacerType<?> PINE = FOLIAGE_PLACERS.register("pine", id -> PineFoliagePlacer.TYPE);
    public static final FoliagePlacerType<?> RANDOM_SPREAD = FOLIAGE_PLACERS.register("random_spread", id -> RandomSpreadFoliagePlacer.TYPE);
    public static final FoliagePlacerType<?> ACACIA = FOLIAGE_PLACERS.register("acacia", id -> AcaciaFoliagePlacer.TYPE);
    public static final FoliagePlacerType<?> DARK_OAK = FOLIAGE_PLACERS.register("dark_oak", id -> DarkOakFoliagePlacer.TYPE);
}