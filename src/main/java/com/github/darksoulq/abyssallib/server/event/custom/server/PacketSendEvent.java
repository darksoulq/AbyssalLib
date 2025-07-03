package com.github.darksoulq.abyssallib.server.event.custom.server;

import com.destroystokyo.paper.event.brigadier.AsyncPlayerSendSuggestionsEvent;
import net.minecraft.network.protocol.Packet;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event fired when a packet is sent from the server to a client.
 * Can be cancelled to prevent the packet from being sent.
 */
public class PacketSendEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    /**
     * The Bukkit player who is the packet recipient.
     */
    private final Player player;

    /**
     * The raw NMS packet being sent.
     */
    private final Packet<?> packet;

    /**
     * Whether the event is cancelled.
     */
    private boolean cancelled;

    /**
     * Constructs a new PacketSendEvent.
     *
     * @param player the Bukkit player to whom the packet is being sent
     * @param packet the NMS packet being sent
     * @param isAsync whether the event is Async
     */
    public PacketSendEvent(Player player, Packet<?> packet, boolean isAsync) {
        super(isAsync);
        this.player = player;
        this.packet = packet;
    }

    /**
     * Gets the player to whom the packet is being sent.
     *
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the packet being sent.
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
