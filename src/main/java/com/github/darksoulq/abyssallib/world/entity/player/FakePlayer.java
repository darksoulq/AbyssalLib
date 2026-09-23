package com.github.darksoulq.abyssallib.world.entity.player;

import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ApiStatus.Experimental
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

        Player bukkitPlayer = this.serverPlayer.getBukkitEntity();
        bukkitPlayer.setCollidable(true);
        bukkitPlayer.setGravity(true);
        bukkitPlayer.setInvulnerable(false);
        bukkitPlayer.setCanPickupItems(true);
        bukkitPlayer.setCustomNameVisible(true);
        bukkitPlayer.setGlowing(false);
        bukkitPlayer.setInvisible(false);
        bukkitPlayer.setSilent(false);

        bukkitPlayer.setSleepingIgnored(true);

        FAKE_PLAYERS.put(this.serverPlayer.getUUID(), this);
    }

    public void spawn() {
        if (spawned) return;

        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();

        ClientboundPlayerInfoUpdatePacket infoPacket = ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(List.of(this.serverPlayer));
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.connection.send(infoPacket);
        }

        server.getPlayerList().getPlayers().add(this.serverPlayer);
        this.serverPlayer.level().addNewPlayer(this.serverPlayer);

        for (Player p : Bukkit.getOnlinePlayers()) {
            this.getPlayer().unlistPlayer(p);
        }

        this.spawned = true;
    }

    public void remove() {
        if (spawned) {
            MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
            server.getPlayerList().getPlayers().remove(this.serverPlayer);

            ClientboundPlayerInfoRemovePacket infoRemovePacket = new ClientboundPlayerInfoRemovePacket(List.of(this.serverPlayer.getUUID()));
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

    public boolean isSpawned() {
        return spawned;
    }

    public void setCollidable(boolean collidable) {
        getPlayer().setCollidable(collidable);
    }

    public boolean isCollidable() {
        return getPlayer().isCollidable();
    }

    public void setGravity(boolean gravity) {
        getPlayer().setGravity(gravity);
    }

    public boolean hasGravity() {
        return getPlayer().hasGravity();
    }

    public void setInvulnerable(boolean invulnerable) {
        getPlayer().setInvulnerable(invulnerable);
    }

    public boolean isInvulnerable() {
        return getPlayer().isInvulnerable();
    }

    public void setGlowing(boolean glowing) {
        getPlayer().setGlowing(glowing);
    }

    public boolean isGlowing() {
        return getPlayer().isGlowing();
    }

    public void setInvisible(boolean invisible) {
        getPlayer().setInvisible(invisible);
    }

    public boolean isInvisible() {
        return getPlayer().isInvisible();
    }

    public void setCanPickupItems(boolean pickup) {
        getPlayer().setCanPickupItems(pickup);
    }

    public boolean canPickupItems() {
        return getPlayer().getCanPickupItems();
    }

    public void setSilent(boolean silent) {
        getPlayer().setSilent(silent);
    }

    public boolean isSilent() {
        return getPlayer().isSilent();
    }

    public EntityEquipment getEquipment() {
        return getPlayer().getEquipment();
    }

    public PlayerInventory getInventory() {
        return getPlayer().getInventory();
    }

    public static void cleanup() {
        for (FakePlayer player : List.copyOf(FAKE_PLAYERS.values())) {
            player.remove();
        }
    }
}