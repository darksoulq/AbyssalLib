package com.github.darksoulq.abyssallib.server.event.custom.server;

import net.minecraft.network.protocol.Packet;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * Event fired when a packet is sent from the server to a client.
 * Can be cancelled to prevent the packet from being sent.
 */
public class PacketSendEvent extends PacketEvent {
    private static final HandlerList HANDLERS = new HandlerList();

    /**
     * Constructs a new PacketSendEvent.
     *
     * @param player the Bukkit player to whom the packet is being sent
     * @param packet the NMS packet being sent
     * @param isAsync whether the event is Async
     */
    public PacketSendEvent(Player player, Packet<?> packet, boolean isAsync) {
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
