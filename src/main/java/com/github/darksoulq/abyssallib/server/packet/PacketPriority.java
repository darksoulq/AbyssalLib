package com.github.darksoulq.abyssallib.server.packet;

/**
 * Represents the priority of a packet handler, similar to Bukkit event priorities.
 * Handlers with higher priority are called later in the pipeline.
 */
public enum PacketPriority {
    LOWEST,
    LOW,
    NORMAL,
    HIGH,
    HIGHEST,
    MONITOR
}
