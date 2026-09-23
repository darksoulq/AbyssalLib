package com.github.darksoulq.abyssallib.world.entity.player;

import com.mojang.authlib.GameProfile;
//? if <=26.2 {
/*import com.mojang.authlib.yggdrasil.ProfileResult;
*///?} else {
import com.mojang.authlib.services.ProfileResult;
//?}
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.Optional;
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

        String profileName = name;
        if (profileName == null) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
            profileName = op.getName();
            if (profileName == null) {
                profileName = "Unknown";
            }
        }
        if (profileName.length() > 16) {
            profileName = profileName.substring(0, 16);
        }

        GameProfile profile = new GameProfile(uuid, profileName);

        Optional<GameProfile> cached = server.services().profileResolver().fetchById(uuid);
        if (cached.isPresent()) {
            profile = cached.get();
        } else {
            ProfileResult profileResult = server.services().sessionService().fetchProfile(uuid, true);
            if (profileResult != null) {
                profile = profileResult.profile();
            }
        }

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

            Object result = ((CraftServer) Bukkit.getServer()).getHandle().playerIo.load(data.serverPlayer.nameAndId());
            if (result != null) {
                CompoundTag tag = null;
                if (result instanceof Optional<?> opt) {
                    if (opt.isPresent()) {
                        tag = (CompoundTag) opt.get();
                    }
                } else if (result instanceof CompoundTag cTag) {
                    tag = cTag;
                }

                if (tag != null) {
                    try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(data.serverPlayer.problemPath(), MinecraftServer.LOGGER)) {
                        ValueInput input = TagValueInput.create(reporter, data.serverPlayer.registryAccess(), tag);
                        data.serverPlayer.load(input);
                    } catch (Exception ignored) {}
                }
            }

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