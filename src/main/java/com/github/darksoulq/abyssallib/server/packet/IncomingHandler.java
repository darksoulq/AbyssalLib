package com.github.darksoulq.abyssallib.server.packet;

import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

/**
 * Handles an incoming packet before it is processed by the Minecraft server.
 * Implementations can inspect, mutate, or cancel the packet.
 *
 * @param <T> the type of packet to handle
 */
@FunctionalInterface
public interface IncomingHandler<T extends Packet<?>> {

    /**
     * Called when a packet is received from the client.
     *
     * @param player the player sending the packet
     * @param packet the received packet (can be modified)
     * @return {@code true} to cancel processing, {@code false} to continue
     */
    boolean handle(@NotNull ServerPlayer player, @NotNull T packet);
}
