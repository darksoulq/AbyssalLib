package com.github.darksoulq.abyssallib.server.event.custom.server;

import net.minecraft.network.protocol.Packet;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * Event fired when a packet is received from a client.
 * Can be cancelled to prevent the packet from being processed by the server.
 */
public class PacketReceiveEvent extends PacketEvent {
    private static final HandlerList HANDLERS = new HandlerList();

    /**
     * Constructs a new PacketReceiveEvent.
     *
     * @param player the Bukkit player who sent the packet
     * @param packet the NMS packet being receive
     * @param isAsync whether the event is Async
     */
    public PacketReceiveEvent(Player player, Packet<?> packet, boolean isAsync) {
        super(player, packet, isAsync);
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
