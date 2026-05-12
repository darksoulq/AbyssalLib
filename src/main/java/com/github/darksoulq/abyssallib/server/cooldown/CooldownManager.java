package com.github.darksoulq.abyssallib.server.cooldown;

import net.kyori.adventure.key.Key;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class CooldownManager {

    private final Map<CooldownScope, Map<Key, Long>> storage = new ConcurrentHashMap<>();

    public long getExpiry(CooldownScope context, Key id) {
        Map<Key, Long> map = storage.get(context);
        return map != null ? map.getOrDefault(id, 0L) : 0L;
    }

    public void setExpiry(CooldownScope context, Key id, long expiry) {
        storage.computeIfAbsent(context, k -> new ConcurrentHashMap<>()).put(id, expiry);
    }

    public void clear(CooldownScope context) {
        storage.remove(context);
    }

    public void cleanup(long currentTime) {
        storage.values().removeIf(map -> {
            map.values().removeIf(expiry -> currentTime >= expiry);
            return map.isEmpty();
        });
    }
}