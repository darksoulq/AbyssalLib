package com.github.darksoulq.abyssallib.world.gen.feature.tree.decorator;

import com.github.darksoulq.abyssallib.common.serialization.Codec;

/**
 * Represents the registered type identifier for a specific tree decorator implementation.
 *
 * @param <P> The specific tree decorator class.
 */
public interface TreeDecoratorType<P extends TreeDecorator> {

    /**
     * Retrieves the codec associated with this tree decorator type for serialization.
     *
     * @return The specific codec instance.
     */
    Codec<P> codec();
}