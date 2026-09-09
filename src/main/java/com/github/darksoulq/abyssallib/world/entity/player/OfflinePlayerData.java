package com.github.darksoulq.abyssallib.world.entity.player;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class OfflinePlayerData {
    private static final Map<UUID, OfflinePlayerData> CACHE = new ConcurrentHashMap<>();

    private final ServerPlayer serverPlayer;
    private final Player bukkitPlayer;
    private boolean active = true;

    private OfflinePlayerData(UUID uuid, String name, Location defaultLocation) {
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        ServerLevel level = defaultLocation != null ? ((CraftWorld) defaultLocation.getWorld()).getHandle() : server.overworld();
        GameProfile profile = new GameProfile(uuid, name != null ? name : uuid.toString());
        this.serverPlayer = new ServerPlayer(server, level, profile, ClientInformation.createDefault());

        Connection connection = new Connection(PacketFlow.SERVERBOUND);
        this.serverPlayer.connection = new NoOpListener(server, connection, this.serverPlayer, CommonListenerCookie.createInitial(profile, false));
        this.bukkitPlayer = this.serverPlayer.getBukkitEntity();
    }

    public static OfflinePlayerData getOrLoad(UUID uuid) {
        return getOrLoad(uuid, null, null);
    }

    public static OfflinePlayerData getOrLoad(UUID uuid, String name, Location defaultLocation) {
        Player online = Bukkit.getPlayer(uuid);
        if (online != null) {
            throw new IllegalStateException("Player is currently online");
        }
        return CACHE.computeIfAbsent(uuid, k -> {
            OfflinePlayerData data = new OfflinePlayerData(uuid, name, defaultLocation);
            ((CraftServer) Bukkit.getServer()).getHandle().playerIo.load(data.serverPlayer.nameAndId());
            return data;
        });
    }

    public void save() {
        if (!active) return;
        if (Bukkit.getPlayer(this.serverPlayer.getUUID()) != null) return;
        ((CraftServer) Bukkit.getServer()).getHandle().playerIo.save(this.serverPlayer);
    }

    public void unload() {
        if (!active) return;
        save();
        active = false;
        CACHE.remove(this.serverPlayer.getUUID());
    }

    public void discard() {
        active = false;
        CACHE.remove(this.serverPlayer.getUUID());
    }

    public Player getPlayer() {
        Player online = Bukkit.getPlayer(this.serverPlayer.getUUID());
        return online != null ? online : bukkitPlayer;
    }

    protected ServerPlayer getServerPlayer() {
        return serverPlayer;
    }

    public static boolean hasData(UUID uuid) {
        return CACHE.containsKey(uuid);
    }

    public static void forceUnload(UUID uuid) {
        OfflinePlayerData data = CACHE.get(uuid);
        if (data != null) {
            data.unload();
        }
    }

    public static void cleanup() {
        for (OfflinePlayerData data : CACHE.values()) {
            data.save();
        }
        CACHE.clear();
    }

    private static class NoOpListener extends ServerGamePacketListenerImpl {
        public NoOpListener(MinecraftServer server, Connection connection, ServerPlayer player, CommonListenerCookie cookie) {
            super(server, connection, player, cookie);
        }

        @Override
        public void send(@NonNull Packet<?> packet) {}
    }
}