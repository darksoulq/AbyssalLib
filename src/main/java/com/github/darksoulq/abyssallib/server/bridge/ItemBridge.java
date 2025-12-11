package com.github.darksoulq.abyssallib.server.bridge;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.server.HookConstants;
import com.github.darksoulq.abyssallib.server.bridge.item.AbyssalLibProvider;
import com.github.darksoulq.abyssallib.server.bridge.item.ItemsAdderProvider;
import com.github.darksoulq.abyssallib.server.bridge.item.MinecraftProvider;
import com.github.darksoulq.abyssallib.server.bridge.item.NexoProvider;
import org.bukkit.inventory.ItemStack;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ItemBridge {
    private static final Map<String, Provider<ItemStack>> PROVIDERS = new HashMap<>();

    public static void setup() {
        register(new MinecraftProvider());
        register(new AbyssalLibProvider());
        if (HookConstants.isEnabled(HookConstants.Plugin.NEXO)) register(new NexoProvider());
        if (HookConstants.isEnabled(HookConstants.Plugin.IA)) register(new ItemsAdderProvider());
    }

    public static boolean hasProvider(String id) {
        if (!Identifier.isValid2Part(id) && !Identifier.isValid3Part(id)) {
            return false;
        }
        Identifier idd = Identifier.of(id);
        return PROVIDERS.containsKey(idd.getKey() == null ? idd.getNamespace() : idd.getKey());
    }
    public static boolean hasProvider(ItemStack item) {
        for (Provider<ItemStack> prov : PROVIDERS.values()) {
            if (prov.belongs(item.asOne())) return true;
        }
        return false;
    }

    public static ItemStack get(String id) {
        if (Identifier.isValid3Part(id) || Identifier.isValid2Part(id)) {
            return get(Identifier.of(id));
        }
        return ItemStack.deserializeBytes(Base64.getDecoder().decode(id));
    }
    public static ItemStack get(Identifier id) {
        if (!PROVIDERS.containsKey(id.getKey()) && !PROVIDERS.containsKey(id.getNamespace())) {
            AbyssalLib.LOGGER.warning("No Provider Found for " + id);
            return null;
        }
        Provider<ItemStack> prov = PROVIDERS.get(id.getKey() == null ? id.getNamespace() : id.getKey());
        if (id.getKey() != null) return prov.get(Identifier.of(id.getNamespace(), id.getPath()));
        return prov.get(id);
    }
    public static Identifier getId(ItemStack stack) {
        for (Map.Entry<String, Provider<ItemStack>> entry : PROVIDERS.entrySet()) {
            Provider<ItemStack> prov = entry.getValue();
            if (!prov.belongs(stack.asOne())) continue;
            Identifier base = prov.getId(stack.asOne());
            if (base == null) return null;
            if (prov instanceof MinecraftProvider) return base;
            return Identifier.of(entry.getKey(), base.getNamespace(), base.getPath());
        }
        return null;
    }
    public static String getIdAsString(ItemStack stack) {
        Identifier id = getId(stack.asOne());
        if (id == null) return asString(stack.asOne());
        return id.toString();
    }
    public static Map<String, Optional<Object>> serializeData(ItemStack stack) {
        Identifier id = getId(stack.asOne());
        if (!hasProvider(stack.asOne()) || id == null) return PROVIDERS.get("minecraft").serializeData(stack);
        Provider<ItemStack> provider = PROVIDERS.get(id.getKey());
        if (provider == null) return PROVIDERS.get("minecraft").serializeData(stack);
        return provider.serializeData(stack);
    }
    public static void deserializeData(Map<String, Optional<Object>> data, ItemStack stack) {
        Identifier id = getId(stack);
        if (!hasProvider(stack) || id == null) {
            PROVIDERS.get("minecraft").deserializeData(data, stack);
            return;
        }
        Provider<ItemStack> provider = PROVIDERS.get(id.getKey());
        if (provider == null) {
            PROVIDERS.get("minecraft").deserializeData(data, stack);
            return;
        }
        provider.deserializeData(data, stack);
    }

    public static String asString(ItemStack item) {
        return Base64.getEncoder().encodeToString(item.serializeAsBytes());
    }
    public static Map<String, Integer> asAmountMap(ItemStack item) {
        Identifier id = getId(item);
        if (id == null) return null;
        return Map.of(id.toString(), item.getAmount());
    }

    public static void register(Provider<ItemStack> provider) {
        PROVIDERS.put(provider.getPrefix(), provider);
    }
}
