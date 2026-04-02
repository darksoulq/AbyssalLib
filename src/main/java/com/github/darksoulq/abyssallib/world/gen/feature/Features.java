package com.github.darksoulq.abyssallib.world.gen.feature;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.registry.DeferredRegistry;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.gen.feature.impl.*;

public class Features {
    public static final DeferredRegistry<Feature<?>> FEATURES = DeferredRegistry.create(Registries.FEATURES, AbyssalLib.PLUGIN_ID);

    public static final Feature<?> ORE = FEATURES.register("ore", id -> new OreFeature());
    public static final Feature<?> SIMPLE_BLOCK = FEATURES.register("simple_block", id -> new SimpleBlockFeature());
    public static final Feature<?> BLOCK_PATCH = FEATURES.register("block_patch", id -> new BlockPatchFeature());
    public static final Feature<?> LAKE = FEATURES.register("lake", id -> new LakeFeature());
    public static final Feature<?> DISK = FEATURES.register("disk", id -> new DiskFeature());
    public static final Feature<?> STRUCTURE = FEATURES.register("structure", id -> new StructureFeature());
    public static final Feature<?> SPRING = FEATURES.register("spring", id -> new SpringFeature());
    public static final Feature<?> GEODE = FEATURES.register("geode", id -> new GeodeFeature());
    public static final Feature<?> PILLAR = FEATURES.register("pillar", id -> new PillarFeature());
    public static final Feature<?> RANDOM_SELECTOR = FEATURES.register("random_selector", id -> new RandomFeature());
    public static final Feature<?> SIMPLE_RANDOM_SELECTOR = FEATURES.register("simple_random_selector", id -> new SimpleRandomFeature());
    public static final Feature<?> RANDOM_BOOLEAN_SELECTOR = FEATURES.register("random_boolean_selector", id -> new RandomBooleanFeature());
    public static final Feature<?> VEGETATION_PATCH = FEATURES.register("vegetation_patch", id -> new VegetationPatchFeature());
    public static final Feature<?> WATERLOGGED_VEGETATION = FEATURES.register("waterlogged_vegetation", id -> new WaterloggedVegetationFeature());
    public static final Feature<?> MULTIFACE_GROWTH = FEATURES.register("multiface_growth", id -> new MultifaceGrowthFeature());
    public static final Feature<?> BLOCK_ATTACHED = FEATURES.register("block_attached", id -> new BlockAttachedFeature());
    public static final Feature<?> DRIPSTONE_CLUSTER = FEATURES.register("dripstone_cluster", id -> new DripstoneClusterFeature());
    public static final Feature<?> FOSSIL = FEATURES.register("fossil", id -> new FossilFeature());
    public static final Feature<?> TREE = FEATURES.register("tree", id -> new TreeFeature());
}