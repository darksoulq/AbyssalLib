package com.github.darksoulq.abyssallib.world.gen.placement;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.registry.DeferredRegistry;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.server.registry.object.Holder;
import com.github.darksoulq.abyssallib.world.gen.placement.modifier.*;

public class PlacementModifiers {
    public static final DeferredRegistry<PlacementModifierType<?>> PLACEMENT_MODIFIERS = DeferredRegistry.create(Registries.PLACEMENT_MODIFIERS, AbyssalLib.PLUGIN_ID);

    public static final Holder<PlacementModifierType<?>> COUNT = PLACEMENT_MODIFIERS.register("count", id -> CountModifier.TYPE);
    public static final Holder<PlacementModifierType<?>> ENVIRONMENT_SCAN = PLACEMENT_MODIFIERS.register("environment_scan", id -> EnvironmentScanModifier.TYPE);
    public static final Holder<PlacementModifierType<?>> HEIGHT_RANGE = PLACEMENT_MODIFIERS.register("height_range", id -> HeightRangeModifier.TYPE);
    public static final Holder<PlacementModifierType<?>> IN_SQUARE = PLACEMENT_MODIFIERS.register("in_square", id -> InSquareModifier.TYPE);
    public static final Holder<PlacementModifierType<?>> RANDOM_OFFSET = PLACEMENT_MODIFIERS.register("random_offset", id -> RandomOffsetModifier.TYPE);
    public static final Holder<PlacementModifierType<?>> RARITY_FILTER = PLACEMENT_MODIFIERS.register("rarity", id -> RarityFilter.TYPE);
    public static final Holder<PlacementModifierType<?>> BIOME_FILTER = PLACEMENT_MODIFIERS.register("biome", id -> BiomeFilterModifier.TYPE);
    public static final Holder<PlacementModifierType<?>> HEIGHTMAP = PLACEMENT_MODIFIERS.register("heightmap", id -> HeightmapModifier.TYPE);
    public static final Holder<PlacementModifierType<?>> COUNT_MULTILAYER = PLACEMENT_MODIFIERS.register("count_multilayer", id -> CountMultilayerModifier.TYPE);
    public static final Holder<PlacementModifierType<?>> SURFACE_WATER_DEPTH = PLACEMENT_MODIFIERS
        .register("surface_water_depth", id -> SurfaceWaterDepthFilter.TYPE);
}
