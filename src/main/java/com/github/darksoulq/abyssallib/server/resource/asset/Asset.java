package com.github.darksoulq.abyssallib.server.resource.asset;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Represents a generic resource pack asset.
 * Assets can emit their file data into a map for resource pack compilation.
 */
@FunctionalInterface
public interface Asset {

    /**
     * Emits the asset's files into the provided output map.
     *
     * @param files the output map where keys are file paths and values are file contents
     */
    void emit(@NotNull Map<String, byte[]> files);
}
