package com.github.darksoulq.abyssallib.server.event.custom.server;

import net.minecraft.network.protocol.Packet;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event fired when a packet is received from a client.
 * Can be cancelled to prevent the packet from being processed by the server.
 */
public class PacketReceiveEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    /**
     * The Bukkit player who sent the packet.
     */
    private final Player player;

    /**
     * The raw NMS packet being received.
     */
    private final Packet<?> packet;

    /**
     * Whether the event is cancelled.
     */
    private boolean cancelled;

    /**
     * Constructs a new PacketReceiveEvent.
     *
     * @param player the Bukkit player who sent the packet
     * @param packet the NMS packet being receive
     * @param isAsync whether the event is Async
     */
    public PacketReceiveEvent(Player player, Packet<?> packet, boolean isAsync) {
        super(isAsync);
        this.player = player;
        this.packet = packet;
    }

    /**
     * Gets the player who sent the packet.
     *
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the packet being received.
     *
     * @return the NMS packet
     */
    public Packet<?> getPacket() {
        return packet;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
