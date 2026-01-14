package com.github.darksoulq.abyssallib.world.structure.processor;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;

public record BlockInfo(Vector pos, Object block, @Nullable ObjectNode nbt) {}