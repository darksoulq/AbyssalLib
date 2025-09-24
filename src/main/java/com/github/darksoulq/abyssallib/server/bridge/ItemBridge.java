package com.github.darksoulq.abyssallib.server.bridge;

import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.server.HookConstants;
import com.github.darksoulq.abyssallib.server.bridge.item.AbyssalLibProvider;
import com.github.darksoulq.abyssallib.server.bridge.item.MinecraftProvider;
import com.github.darksoulq.abyssallib.server.bridge.item.NexoProvider;
import org.bukkit.inventory.ItemStack;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class ItemBridge {
    private static final Map<String, Provider<ItemStack>> PROVIDERS = new HashMap<>();

    public static void setup() {
        register(new MinecraftProvider());
        register(new AbyssalLibProvider());
        if (HookConstants.isEnabled(HookConstants.Plugin.NEXO)) register(new NexoProvider());
    }

    public static boolean hasProvider(String id) {
        String[] parts = id.split(":", 3);
        if (!Identifier.isValid2Part(id) && !Identifier.isValid3Part(id)) {
            return false;
        }
        return PROVIDERS.containsKey(parts[0]);
    }
    public static boolean hasProvider(ItemStack item) {
        for (Provider<ItemStack> prov : PROVIDERS.values()) {
            if (prov.belongs(item)) return true;
        }
        return false;
    }

    public static ItemStack get(String id) {
        if (Identifier.isValid3Part(id) || Identifier.isValid2Part(id)) {
            String[] parts = id.split(":", 3);
            if (parts.length == 2) {
                return get(Identifier.of(parts[0], parts[1]));
            } else {
                return get(Identifier.of(parts[0], parts[1], parts[2]));
            }
        }
        return ItemStack.deserializeBytes(Base64.getDecoder().decode(id));
    }
    public static ItemStack get(Identifier id) {
        if (!PROVIDERS.containsKey(id.key()) && !PROVIDERS.containsKey(id.namespace())) return null;
        Provider<ItemStack> prov = PROVIDERS.get(id.key() == null ? id.namespace() : id.key());
        return prov.get(id);
    }
    public static Identifier getId(ItemStack stack) {
        for (Map.Entry<String, Provider<ItemStack>> entry : PROVIDERS.entrySet()) {
            Provider<ItemStack> prov = entry.getValue();
            if (!prov.belongs(stack)) continue;
            Identifier base = prov.getId(stack);
            if (base == null) return null;
            if (prov instanceof MinecraftProvider) return base;
            return Identifier.of(entry.getKey() + ":" + base.namespace(), base.path());
        }
        return null;
    }
    public static String getIdAsString(ItemStack stack) {
        Identifier id = getId(stack);
        if (id == null) return asString(stack);
        return id.toString();
    }

    public static String asString(ItemStack item) {
        return Base64.getEncoder().encodeToString(item.serializeAsBytes());
    }
    public static Map<String, Integer> asAmountMap(ItemStack item) {
        for (Provider<ItemStack> prov : PROVIDERS.values()) {
            if (!prov.belongs(item)) continue;
            Identifier id = prov.getId(item);
            int amount = item.getAmount();
            return Map.of(id.toString(), amount);
        }
        return null;
    }

    public static void register(Provider<ItemStack> provider) {
        PROVIDERS.put(provider.getPrefix(), provider);
    }
}
