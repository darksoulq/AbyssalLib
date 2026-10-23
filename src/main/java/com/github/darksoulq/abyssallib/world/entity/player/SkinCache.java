package com.github.darksoulq.abyssallib.world.entity.player;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.config.Config;
import com.github.darksoulq.abyssallib.server.scheduler.Clock;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
//? if <=26.2 {
/*import com.mojang.authlib.yggdrasil.ProfileResult;
 *///?} else {
import com.mojang.authlib.services.ProfileResult;
//?}
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;

import java.io.File;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class SkinCache {
    private static final Map<UUID, CachedSkin> CACHE = new ConcurrentHashMap<>();
    private static final File DIRECTORY = new File("plugins/AbyssalLib/skins_cache");
    private static final long ONE_DAY = 24L * 60 * 60 * 1000L;
    private static final long MEMORY_TTL = 2L * 60 * 60 * 1000L;

    public static void init() {
        AbyssalLib.SCHEDULER.schedule(SkinCache::sweepMemory).after(72000, Clock.TICKS).repeatEvery(72000L, Clock.TICKS);
    }

    public static void updateCacheAsync(GameProfile profile) {
        String tempVal = "";
        String tempSig = "";

        for (Property prop : profile.properties().get("textures")) {
            tempVal = prop.value();
            tempSig = prop.signature();
            break;
        }

        if (tempVal.isEmpty()) return;

        final String val = tempVal;
        final String sig = tempSig;
        final UUID uuid = profile.id();
        final long now = System.currentTimeMillis();

        CACHE.put(uuid, new CachedSkin(now, val, sig, now));

        CompletableFuture.runAsync(() -> {
            Config config = new Config(DIRECTORY, uuid.toString());
            config.value("timestamp", now).set(now);
            config.value("value", "").set(val);
            config.value("signature", "").set(sig);
            config.save();
        });
    }

    public static CompletableFuture<Void> fetchSkinAsync(GameProfile profile, boolean skipSessionServer) {
        return CompletableFuture.runAsync(() -> fetchSkinSync(profile, skipSessionServer));
    }

    public static void fetchSkinSync(GameProfile profile, boolean skipSessionServer) {
        UUID uuid = profile.id();
        long now = System.currentTimeMillis();
        CachedSkin memorySkin = CACHE.get(uuid);

        if (memorySkin == null) {
            Config config = new Config(DIRECTORY, uuid.toString());
            long timestamp = config.value("timestamp", 0L).get();
            if (timestamp > 0) {
                String value = config.value("value", "").get();
                String signature = config.value("signature", "").get();
                memorySkin = new CachedSkin(timestamp, value, signature, now);
                CACHE.put(uuid, memorySkin);
            }
        } else {
            memorySkin.lastAccessed = now;
        }

        if (memorySkin != null && (skipSessionServer || (now - memorySkin.timestamp) <= ONE_DAY)) {
            if (!memorySkin.value.isEmpty()) {
                profile.properties().removeAll("textures");
                profile.properties().put("textures", new Property("textures", memorySkin.value, memorySkin.signature));
            }
            return;
        }

        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        Optional<GameProfile> resolved = server.services().profileResolver().fetchById(uuid);
        GameProfile fetched = null;

        if (resolved.isPresent()) {
            fetched = resolved.get();
        } else if (!skipSessionServer) {
            ProfileResult profileResult = server.services().sessionService().fetchProfile(uuid, true);
            if (profileResult != null) {
                fetched = profileResult.profile();
            }
        }

        if (fetched != null) {
            String tempVal = "";
            String tempSig = "";

            for (Property prop : fetched.properties().get("textures")) {
                tempVal = prop.value();
                tempSig = prop.signature();
                break;
            }

            final String val = tempVal;
            final String sig = tempSig;

            if (!val.isEmpty()) {
                profile.properties().removeAll("textures");
                profile.properties().put("textures", new Property("textures", val, sig));
            }

            CACHE.put(uuid, new CachedSkin(now, val, sig, now));

            Config config = new Config(DIRECTORY, uuid.toString());
            config.value("timestamp", now).set(now);
            config.value("value", "").set(val);
            config.value("signature", "").set(sig);
            config.save();
        }
    }

    public static void sweepMemory() {
        long now = System.currentTimeMillis();
        CACHE.entrySet().removeIf(entry -> (now - entry.getValue().lastAccessed) > MEMORY_TTL);
    }

    public static void unloadMemory(UUID uuid) {
        CACHE.remove(uuid);
    }

    private static class CachedSkin {
        final long timestamp;
        final String value;
        final String signature;
        long lastAccessed;

        CachedSkin(long timestamp, String value, String signature, long lastAccessed) {
            this.timestamp = timestamp;
            this.value = value;
            this.signature = signature;
            this.lastAccessed = lastAccessed;
        }
    }
}