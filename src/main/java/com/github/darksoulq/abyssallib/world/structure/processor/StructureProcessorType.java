package com.github.darksoulq.abyssallib.world.structure.processor;

import com.github.darksoulq.abyssallib.common.serialization.Codec;

public interface StructureProcessorType<P extends StructureProcessor> {
    Codec<P> codec();
}