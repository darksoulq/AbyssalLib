package com.github.darksoulq.abyssallib.server.packet;

import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;

public final class PacketIO {
    private PacketIO() {}

    public static void send(ServerPlayer player, Packet<?> packet) {
        player.connection.send(packet);
    }

    public static void send(Iterable<ServerPlayer> players, Packet<?> packet) {
        for (ServerPlayer player : players) {
            send(player, packet);
        }
    }
}
