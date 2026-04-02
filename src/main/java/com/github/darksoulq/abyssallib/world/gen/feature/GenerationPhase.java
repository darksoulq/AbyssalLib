package com.github.darksoulq.abyssallib.world.gen.feature;

/**
 * Represents the chronological steps in which features are placed during chunk generation.
 * <p>
 * The order of these enum constants strictly dictates the execution order in the
 * custom block populator, mirroring the vanilla Minecraft generation pipeline.
 */
public enum GenerationPhase {
    RAW_GENERATION,
    LAKES,
    LOCAL_MODIFICATIONS,
    UNDERGROUND_STRUCTURES,
    SURFACE_STRUCTURES,
    STRONGHOLDS,
    UNDERGROUND_ORES,
    UNDERGROUND_DECORATION,
    FLUID_SPRINGS,
    VEGETAL_DECORATION,
    TOP_LAYER_MODIFICATION
}