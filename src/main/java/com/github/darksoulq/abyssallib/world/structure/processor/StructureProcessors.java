package com.github.darksoulq.abyssallib.world.structure.processor;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.registry.DeferredRegistry;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.structure.processor.impl.BlockIgnoreProcessor;
import com.github.darksoulq.abyssallib.world.structure.processor.impl.IntegrityProcessor;

public class StructureProcessors {
    public static final DeferredRegistry<StructureProcessorType<?>> STRUCTURE_PROCESSORS = DeferredRegistry.create(Registries.PROCESSOR_TYPES, AbyssalLib.PLUGIN_ID);

    public static final StructureProcessorType<?> INTEGRITY_PROCESSOR = STRUCTURE_PROCESSORS.register("integrity", id -> IntegrityProcessor.TYPE);
    public static final StructureProcessorType<?> BLOCK_IGNORE_PROCESSOR = STRUCTURE_PROCESSORS.register("block_ignore", id -> BlockIgnoreProcessor.TYPE);
}
