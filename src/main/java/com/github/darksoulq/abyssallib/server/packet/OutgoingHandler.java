package com.github.darksoulq.abyssallib.server.packet;

import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

/**
 * Handles an outgoing packet before it is sent to the client.
 * Implementations can inspect, mutate, or cancel the packet.
 *
 * @param <T> the type of packet to handle
 */
@FunctionalInterface
public interface OutgoingHandler<T extends Packet<?>> {

    /**
     * Called when a packet is about to be sent to a player.
     *
     * @param player the target player
     * @param packet the packet (can be modified)
     * @return {@code true} to cancel sending, {@code false} to send normally
     */
    boolean handle(@NotNull ServerPlayer player, @NotNull T packet);
}
