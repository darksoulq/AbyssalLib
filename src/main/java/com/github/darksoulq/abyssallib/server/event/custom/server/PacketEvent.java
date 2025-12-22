package com.github.darksoulq.abyssallib.server.event.custom.server;

import net.minecraft.network.protocol.Packet;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public abstract class PacketEvent extends Event implements Cancellable {

    /**
     * The Bukkit player who sent the packet.
     */
    private final Player player;

    /**
     * The raw NMS packet being received.
     */
    private Packet<?> packet;

    /**
     * Whether the event is cancelled.
     */
    private boolean cancelled = false;

    public PacketEvent(Player player, Packet<?> packet, boolean isAsync) {
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
    /**
     * Sets the packet being received.
     *
     * @param packet the NMS packet
     */
    public void setPacket(Packet<?> packet) {
        this.packet = packet;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
