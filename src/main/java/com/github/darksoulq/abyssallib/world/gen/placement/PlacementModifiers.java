package com.github.darksoulq.abyssallib.world.gen.placement;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.registry.DeferredRegistry;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.gen.placement.modifier.*;

public class PlacementModifiers {
    public static final DeferredRegistry<PlacementModifierType<?>> PLACEMENT_MODIFIERS = DeferredRegistry.create(Registries.PLACEMENT_MODIFIERS, AbyssalLib.PLUGIN_ID);

    public static final PlacementModifierType<?> COUNT = PLACEMENT_MODIFIERS.register("count", id -> CountModifier.TYPE);
    public static final PlacementModifierType<?> HEIGHT_RANGE = PLACEMENT_MODIFIERS.register("height_range", id -> HeightRangeModifier.TYPE);
    public static final PlacementModifierType<?> IN_SQUARE = PLACEMENT_MODIFIERS.register("in_square", id -> InSquareModifier.TYPE);
    public static final PlacementModifierType<?> ENVIRONMENT_SCAN = PLACEMENT_MODIFIERS.register("environment_scan", id -> EnvironmentScanModifier.TYPE);
    public static final PlacementModifierType<?> BLOCK_FILTER = PLACEMENT_MODIFIERS.register("block_filter", id -> BlockFilterModifier.TYPE);
    public static final PlacementModifierType<?> BIOME_FILTER = PLACEMENT_MODIFIERS.register("biome_filter", id -> BiomeFilterModifier.TYPE);
    public static final PlacementModifierType<?> CHANCE = PLACEMENT_MODIFIERS.register("chance", id -> ChanceModifier.TYPE);
    public static final PlacementModifierType<?> RANDOM_OFFSET = PLACEMENT_MODIFIERS.register("random_offset", id -> RandomOffsetModifier.TYPE);
    public static final PlacementModifierType<?> HEIGHTMAP = PLACEMENT_MODIFIERS.register("heightmap", id -> HeightmapModifier.TYPE);
    public static final PlacementModifierType<?> RARITY_FILTER = PLACEMENT_MODIFIERS.register("rarity_filter", id -> RarityFilterModifier.TYPE);
    public static final PlacementModifierType<?> WATER_DEPTH_FILTER = PLACEMENT_MODIFIERS.register("water_depth_filter", id -> WaterDepthFilterModifier.TYPE);
    public static final PlacementModifierType<?> NOISE_THRESHOLD = PLACEMENT_MODIFIERS.register("noise_threshold", id -> NoiseThresholdModifier.TYPE);
    public static final PlacementModifierType<?> NOISE_COUNT = PLACEMENT_MODIFIERS.register("noise_count", id -> NoiseCountModifier.TYPE);
    public static final PlacementModifierType<?> SURFACE_RELATIVE_THRESHOLD = PLACEMENT_MODIFIERS.register("surface_relative_threshold", id -> SurfaceRelativeThresholdModifier.TYPE);
    public static final PlacementModifierType<?> BIOME_TRANSITION = PLACEMENT_MODIFIERS.register("biome_transition", id -> BiomeTransitionModifier.TYPE);
    public static final PlacementModifierType<?> COUNT_ON_EVERY_LAYER = PLACEMENT_MODIFIERS.register("count_on_every_layer", id -> CountOnEveryLayerModifier.TYPE);
    public static final PlacementModifierType<?> FIXED_PLACEMENT = PLACEMENT_MODIFIERS.register("fixed_placement", id -> FixedPlacementModifier.TYPE);
}