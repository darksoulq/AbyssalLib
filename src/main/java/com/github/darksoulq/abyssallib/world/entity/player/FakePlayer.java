package com.github.darksoulq.abyssallib.world.entity.player;

import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FakePlayer {
    public static final Map<UUID, FakePlayer> FAKE_PLAYERS = new ConcurrentHashMap<>();

    private final OfflinePlayerData offlineData;
    private final ServerPlayer serverPlayer;
    private boolean spawned = false;

    public FakePlayer(UUID uuid, String name, Location location) {
        this(OfflinePlayerData.getOrLoad(uuid, name, location), location);
    }

    public FakePlayer(OfflinePlayerData data, Location location) {
        this.offlineData = data;
        this.serverPlayer = data.getServerPlayer();

        this.serverPlayer.setPos(location.getX(), location.getY(), location.getZ());
        this.serverPlayer.setYRot(location.getYaw());
        this.serverPlayer.setXRot(location.getPitch());
        this.serverPlayer.setYHeadRot(location.getYaw());

        FAKE_PLAYERS.put(this.serverPlayer.getUUID(), this);
    }

    public void spawn() {
        if (spawned) return;
        ClientboundPlayerInfoUpdatePacket infoPacket = ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(List.of(this.serverPlayer));

        ClientboundAddEntityPacket addPacket = new ClientboundAddEntityPacket(
            this.serverPlayer, 0, this.serverPlayer.blockPosition()
        );

        ClientboundRotateHeadPacket headPacket = new ClientboundRotateHeadPacket(
            this.serverPlayer, (byte) ((this.serverPlayer.getYHeadRot() * 256.0F) / 360.0F));

        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.connection.send(infoPacket);
            player.connection.send(addPacket);
            var entityData = this.serverPlayer.getEntityData().getNonDefaultValues();
            if (entityData != null) {
                player.connection.send(new ClientboundSetEntityDataPacket(this.serverPlayer.getId(), entityData));
            }
            player.connection.send(headPacket);
        }

        this.serverPlayer.level().addNewPlayer(this.serverPlayer);
        this.spawned = true;
    }

    public void remove() {
        if (spawned) {
            ClientboundPlayerInfoRemovePacket infoRemovePacket = new ClientboundPlayerInfoRemovePacket(List.of(this.serverPlayer.getUUID()));
            MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                player.connection.send(infoRemovePacket);
            }
            this.serverPlayer.level().removePlayerImmediately(this.serverPlayer, net.minecraft.world.entity.Entity.RemovalReason.DISCARDED);
            this.spawned = false;
        }

        this.serverPlayer.discard();
        if (this.offlineData != null) {
            this.offlineData.unload();
        }
        FAKE_PLAYERS.remove(this.serverPlayer.getUUID());
    }

    public Player getPlayer() {
        return this.offlineData.getPlayer();
    }

    public OfflinePlayerData getOfflineData() {
        return offlineData;
    }

    public static void cleanup() {
        for (FakePlayer player : List.copyOf(FAKE_PLAYERS.values())) {
            player.remove();
        }
    }
}