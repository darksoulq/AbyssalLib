package com.github.darksoulq.abyssallib.server.packet;

import net.minecraft.network.protocol.Packet;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

/**
 * Utility for low-level packet transmission to clients.
 * <p>
 * This class bypasses the standard Bukkit API to send raw NMS packets directly
 * via the player's network connection.
 */
public final class PacketIO {

    /**
     * Sends a single NMS packet to a specific player.
     *
     * @param player The recipient player.
     * @param packet The {@link Packet} instance to send.
     */
    public static void send(Player player, Packet<?> packet) {
        ((CraftPlayer) player).getHandle().connection.send(packet);
    }

    /**
     * Sends a packet to multiple players.
     *
     * @param players An iterable collection of players.
     * @param packet  The packet to distribute.
     */
    public static void send(Iterable<Player> players, Packet<?> packet) {
        for (Player player : players) {
            send(player, packet);
        }
    }

    /**
     * Broadcasts a packet to every player currently online.
     *
     * @param packet The packet to broadcast.
     */
    public static void broadcast(Packet<?> packet) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            send(player, packet);
        }
    }
}