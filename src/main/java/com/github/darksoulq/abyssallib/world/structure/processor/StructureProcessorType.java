package com.github.darksoulq.abyssallib.world.structure.processor;

import com.github.darksoulq.abyssallib.common.serialization.Codec;

/**
 * A registry-friendly type definition for a {@link StructureProcessor}.
 *
 * @param <P> The specific implementation of StructureProcessor.
 */
public interface StructureProcessorType<P extends StructureProcessor> {
    /**
     * @return The codec used to handle data for this processor type.
     */
    Codec<P> codec();
}