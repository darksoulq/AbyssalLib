package com.github.darksoulq.abyssallib.world.structure.processor;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;

/**
 * A data container representing a specific block within a structure during the placement process.
 * @param pos          The transformed position vector where the block will be placed.
 * @param block        The actual block object (typically {@link org.bukkit.block.data.BlockData} or {@link com.github.darksoulq.abyssallib.world.block.CustomBlock}).
 * @param combinedData The serialized visual state data of the block.
 * @param nbt          The serialized tile entity or property data for the block.
 */
public record BlockInfo(Vector pos, Object block, @Nullable ObjectNode combinedData, @Nullable ObjectNode nbt) {
}