package com.github.darksoulq.abyssallib.server.bridge;

import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.server.HookConstants;
import com.github.darksoulq.abyssallib.server.bridge.block.AbyssalLibProvider;
import com.github.darksoulq.abyssallib.server.bridge.block.BridgeBlock;
import com.github.darksoulq.abyssallib.server.bridge.block.MinecraftProvider;
import com.github.darksoulq.abyssallib.server.bridge.block.NexoBlockProvider;
import org.bukkit.inventory.ItemStack;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class BlockBridge {
    private static final Map<String, Provider<BridgeBlock<?>>> PROVIDERS = new HashMap<>();

    public static void setup() {
        register(new MinecraftProvider());
        register(new AbyssalLibProvider());
        register(new NexoBlockProvider());
    }

    public static boolean hasProvider(String id) {
        String[] parts = id.split(":", 3);
        if (!Identifier.isValid2Part(id) && !Identifier.isValid3Part(id)) {
            return false;
        }
        return PROVIDERS.containsKey(parts[0]);
    }
    public static boolean hasProvider(BridgeBlock<?> block) {
        for (Provider<BridgeBlock<?>> prov : PROVIDERS.values()) {
            if (prov.belongs(block)) return true;
        }
        return false;
    }

    public static BridgeBlock<?> get(String id) {
        if (Identifier.isValid(id)) {
            String[] parts = id.split(":", 3);
            if (parts.length == 2) {
                return get(Identifier.of(parts[0], parts[1]));
            } else {
                return get(Identifier.of(parts[0], parts[1], parts[2]));
            }
        }
        return null;
    }
    public static BridgeBlock<?> get(Identifier id) {
        if (!PROVIDERS.containsKey(id.getKey()) && !PROVIDERS.containsKey(id.getNamespace())) return null;
        Provider<BridgeBlock<?>> prov = PROVIDERS.get(id.getKey() == null ? id.getNamespace() : id.getKey());
        return prov.get(id);
    }

    public static void register(Provider<BridgeBlock<?>> provider) {
        PROVIDERS.put(provider.getPrefix(), provider);
    }
}
