package com.github.darksoulq.abyssallib.server.bridge;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.server.bridge.block.AbyssalLibProvider;
import com.github.darksoulq.abyssallib.server.bridge.block.ItemsAdderProvider;
import com.github.darksoulq.abyssallib.server.bridge.block.MinecraftProvider;
import com.github.darksoulq.abyssallib.server.bridge.block.NexoBlockProvider;
import com.github.darksoulq.abyssallib.server.util.HookConstants;
import com.github.darksoulq.abyssallib.world.block.CustomBlock;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockMechanic;
import org.bukkit.NamespacedKey;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class BlockBridge {
    public static final String ID_MINECRAFT = "minecraft";
    public static final String ID_ABYSSAL = "abyssallib";
    public static final String ID_NEXO = "nexo";
    public static final String ID_IA = "ia";

    public static final MinecraftProvider MINECRAFT = new MinecraftProvider();
    public static final AbyssalLibProvider ABYSSAL_LIB = new AbyssalLibProvider();
    public static NexoBlockProvider NEXO;
    public static ItemsAdderProvider IA;

    public static void setup() {
        if (HookConstants.isEnabled(HookConstants.Plugin.NEXO)) NEXO = new NexoBlockProvider();
        if (HookConstants.isEnabled(HookConstants.Plugin.IA)) IA = new ItemsAdderProvider();
    }

    @SuppressWarnings("unchecked")
    public static <D, T> @Nullable Map<D, D> serialize(@NotNull DynamicOps<D> ops, @NotNull T block) {
        try {
            Identifier bridgeId = getBridgeId(block);
            if (bridgeId == null) return null;
            BlockProvider<T> prov = (BlockProvider<T>) getProvider(bridgeId.getKey() == null ? bridgeId.getNamespace() : bridgeId.getKey());
            if (prov == null) return null;
            return prov.serializeData(block, ops);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static <D, T> BridgeBlock<T> deserialize(@NotNull DynamicOps<D> ops, @NotNull Map<D, D> data, @NotNull BridgeBlock<T> block) {
        BlockProvider<T> prov = (BlockProvider<T>) getProvider(block.provider());
        if (prov == null) return null;
        return prov.deserializeData(data, block, ops);
    }

    public static boolean hasProvider(@Nullable String id) {
        if (id == null) return false;
        if (!id.contains(":")) {
            return getProvider(id) != null;
        }

        try {
            Identifier identifier = Identifier.of(id);
            return hasProvider(identifier);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    public static boolean hasProvider(@NotNull BridgeBlock<?> block) {
        return getProvider(block.provider()) != null;
    }
    public static boolean hasProvider(@NotNull Identifier id) {
        String providerKey = resolveProviderKey(id);
        return getProvider(providerKey) != null;
    }

    public static @Nullable BridgeBlock<?> get(@Nullable String id) {
        if (id == null) return null;
        try {
            return get(Identifier.of(id));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    public static @Nullable BridgeBlock<?> get(@NotNull Identifier id) {
        String key = resolveProviderKey(id);
        BlockProvider<?> prov = getProvider(key);
        if (prov == null) return null;

        return prov.get(id);
    }
    @SuppressWarnings("unchecked")
    public static <T> @Nullable BridgeBlock<T> get(@Nullable T value) {
        switch (value) {
            case null -> {
                return null;
            }
            case BlockData data -> {
                return (BridgeBlock<T>) get(Identifier.of(ID_MINECRAFT, data.getMaterial().toString().toLowerCase()));
            }
            case CustomBlock abyssal -> {
                return (BridgeBlock<T>) get(Identifier.of(AbyssalLib.PLUGIN_ID, abyssal.getId().getNamespace(), abyssal.getId().getPath()));
            }
            default -> {
            }
        }

        if (HookConstants.isEnabled(HookConstants.Plugin.IA) && value instanceof dev.lone.itemsadder.api.CustomBlock ia) {
            NamespacedKey key = NamespacedKey.fromString(ia.getNamespacedID());
            if (key != null) {
                return (BridgeBlock<T>) get(Identifier.of(ID_IA, key.getNamespace(), key.getKey()));
            }
        }
        if (HookConstants.isEnabled(HookConstants.Plugin.NEXO) && value instanceof CustomBlockMechanic nexo) {
            NamespacedKey key = NamespacedKey.fromString(nexo.getItemID());
            if (key != null) {
                return (BridgeBlock<T>) get(Identifier.of(ID_NEXO, key.getNamespace(), key.getKey()));
            }
        }
        return null;
    }

    public static @Nullable Identifier getId(@NotNull BridgeBlock<?> block) {
        return block.id();
    }
    public static @NotNull String getIdAsString(@NotNull BridgeBlock<?> block) {
        Identifier id = getId(block);
        return id == null ? "" : id.toString();
    }

    private static <T> Identifier getBridgeId(T value) {
        switch (value) {
            case null -> {
                return null;
            }
            case BlockData data -> {
                return Identifier.of(ID_MINECRAFT, data.getMaterial().toString().toLowerCase());
            }
            case CustomBlock abyssal -> {
                return Identifier.of(AbyssalLib.PLUGIN_ID, abyssal.getId().getNamespace(), abyssal.getId().getPath());
            }
            default -> {}
        }

        if (HookConstants.isEnabled(HookConstants.Plugin.IA) && value instanceof dev.lone.itemsadder.api.CustomBlock ia) {
            NamespacedKey key = NamespacedKey.fromString(ia.getNamespacedID());
            if (key != null) {
                return Identifier.of(ID_IA, key.getNamespace(), key.getKey());
            }
        }
        if (HookConstants.isEnabled(HookConstants.Plugin.NEXO) && value instanceof CustomBlockMechanic nexo) {
            NamespacedKey key = NamespacedKey.fromString(nexo.getItemID());
            if (key != null) {
                return Identifier.of(ID_NEXO, key.getNamespace(), key.getKey());
            }
        }
        return null;
    }
    private static String resolveProviderKey(@NotNull Identifier id) {
        return id.getKey() == null ? id.getNamespace() : id.getKey();
    }
    public static @Nullable BlockProvider<?> getProvider(@Nullable String id) {
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