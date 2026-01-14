package com.github.darksoulq.abyssallib.server.bridge;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.server.bridge.item.AbyssalLibProvider;
import com.github.darksoulq.abyssallib.server.bridge.item.ItemsAdderProvider;
import com.github.darksoulq.abyssallib.server.bridge.item.MinecraftProvider;
import com.github.darksoulq.abyssallib.server.bridge.item.NexoProvider;
import com.github.darksoulq.abyssallib.server.util.HookConstants;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Base64;
import java.util.Map;
import java.util.Optional;

public class ItemBridge {
    public static final String ID_MINECRAFT = "minecraft";
    public static final String ID_ABYSSAL = "abyssallib";
    public static final String ID_NEXO = "nexo";
    public static final String ID_IA = "ia";

    public static final MinecraftProvider MINECRAFT = new MinecraftProvider();
    public static final AbyssalLibProvider ABYSSAL_LIB = new AbyssalLibProvider();
    public static NexoProvider NEXO;
    public static ItemsAdderProvider IA;

    public static void setup() {
        if (HookConstants.isEnabled(HookConstants.Plugin.NEXO)) NEXO = new NexoProvider();
        if (HookConstants.isEnabled(HookConstants.Plugin.IA)) IA = new ItemsAdderProvider();
    }

    public static boolean hasProvider(@Nullable String id) {
        if (id == null) return false;
        if (!Identifier.isValid2Part(id) && !Identifier.isValid3Part(id)) {
            return false;
        }
        try {
            return hasProvider(Identifier.of(id));
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean hasProvider(@NotNull Identifier id) {
        String providerKey = resolveProviderKey(id);
        return getProvider(providerKey) != null;
    }

    public static boolean hasProvider(@NotNull ItemStack item) {
        if (ABYSSAL_LIB.belongs(item.asOne())) return true;
        if (NEXO != null && NEXO.belongs(item.asOne())) return true;
        return IA != null && IA.belongs(item.asOne());
    }

    public static @Nullable ItemStack get(String id) {
        if (Identifier.isValid3Part(id) || Identifier.isValid2Part(id)) {
            return get(Identifier.of(id));
        }
        try {
            return ItemStack.deserializeBytes(Base64.getDecoder().decode(id));
        } catch (Exception e) {
            return null;
        }
    }

    public static @Nullable ItemStack get(@NotNull Identifier id) {
        String key = resolveProviderKey(id);
        ItemProvider prov = getProvider(key);

        if (prov == null) {
            AbyssalLib.LOGGER.warning("No Provider Found for " + id);
            return null;
        }
        if (id.getKey() != null) {
            return prov.get(Identifier.of(id.getNamespace(), id.getPath()));
        }
        return prov.get(id);
    }

    public static @Nullable Identifier getId(@NotNull ItemStack stack) {
        ItemStack single = stack.asOne();
        if (ABYSSAL_LIB.belongs(single)) return wrapId(ID_ABYSSAL, ABYSSAL_LIB.getId(single));
        if (NEXO != null && NEXO.belongs(single)) return wrapId(ID_NEXO, NEXO.getId(single));
        if (IA != null && IA.belongs(single)) return wrapId(ID_IA, IA.getId(single));
        if (MINECRAFT.belongs(single)) return MINECRAFT.getId(single);

        return null;
    }

    private static Identifier wrapId(String providerKey, Identifier base) {
        if (base == null) return null;
        return Identifier.of(providerKey, base.getNamespace(), base.getPath());
    }

    public static String getIdAsString(@NotNull ItemStack stack) {
        Identifier id = getId(stack.asOne());
        if (id == null) return asString(stack.asOne());
        return id.toString();
    }

    public static Map<String, Optional<Object>> serializeData(ItemStack stack, DynamicOps<?> ops) {
        Identifier id = getId(stack.asOne());
        if (id == null) return MINECRAFT.serializeData(stack, ops);

        String key = resolveProviderKey(id);
        ItemProvider provider = getProvider(key);
        if (provider == null) return MINECRAFT.serializeData(stack, ops);

        return provider.serializeData(stack, ops);
    }

    public static <T> void deserializeData(Map<String, Optional<T>> data, ItemStack stack, DynamicOps<T> ops) {
        Identifier id = getId(stack);

        if (id == null) {
            MINECRAFT.deserializeData(data, stack, ops);
            return;
        }

        String key = resolveProviderKey(id);
        ItemProvider provider = getProvider(key);

        if (provider == null) {
            MINECRAFT.deserializeData(data, stack, ops);
            return;
        }

        provider.deserializeData(data, stack, ops);
    }

    public static String asString(ItemStack item) {
        return Base64.getEncoder().encodeToString(item.serializeAsBytes());
    }

    public static Map<String, Integer> asAmountMap(ItemStack item) {
        Identifier id = getId(item);
        if (id == null) return null;
        return Map.of(id.toString(), item.getAmount());
    }

    private static String resolveProviderKey(@NotNull Identifier id) {
        return id.getKey() == null ? id.getNamespace() : id.getKey();
    }

    public static @Nullable ItemProvider getProvider(@Nullable String id) {
        if (id == null) return null;
        return switch (id) {
            case ID_MINECRAFT -> MINECRAFT;
            case ID_ABYSSAL -> ABYSSAL_LIB;
            case ID_NEXO -> HookConstants.isEnabled(HookConstants.Plugin.NEXO) ? NEXO : null;
            case ID_IA -> HookConstants.isEnabled(HookConstants.Plugin.IA) ? IA : null;
            default -> null;
        };
    }
}