package com.github.darksoulq.abyssallib.world.gen.feature;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.registry.DeferredRegistry;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.server.registry.object.Holder;
import com.github.darksoulq.abyssallib.world.gen.feature.impl.*;

public class Features {
    public static final DeferredRegistry<Feature<?>> FEATURES = DeferredRegistry.create(Registries.FEATURES, AbyssalLib.PLUGIN_ID);

    public static final Feature<?> SIMPLE_BLOCK = FEATURES.register("simple_block", id -> new SimpleBlockFeature());
    public static final Feature<?> BLOCK_PILE = FEATURES.register("block_pile", id -> new BlockPileFeature());
    public static final Feature<?> CAVE_VINE = FEATURES.register("cave_vine", id -> new BlockPileFeature());
    public static final Feature<?> RANDOM_PATCH = FEATURES.register("random_patch", id -> new RandomPatchFeature());
    public static final Feature<?> ORE = FEATURES.register("ore", id -> new OreFeature());
    public static final Feature<?> DISK = FEATURES.register("disk", id -> new DiskFeature());
    public static final Feature<?> GEODE = FEATURES.register("geode", id -> new GeodeFeature());
    public static final Feature<?> SPRING = FEATURES.register("spring", id -> new SpringFeature());
    public static final Feature<?> STRUCTURE = FEATURES.register("structure", id -> new StructureFeature());
    public static final Feature<?> VEGETATION = FEATURES.register("vegetation_patch", id -> new VegetationPatchFeature());
}
