package com.github.darksoulq.abyssallib.server.packet;

import net.minecraft.network.protocol.BundlePacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

public final class PacketIO {
    public static void send(Player player, Packet<?> packet) {
        ((CraftPlayer) player).getHandle().connection.send(packet);
    }

    public static void send(Iterable<Player> players, Packet<?> packet) {
        for (Player player : players) {
            send(player, packet);
        }
    }

    public static void broadcast(Packet<?> packet) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            send(player, packet);
        }
    }
}
