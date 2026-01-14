package com.github.darksoulq.abyssallib.server.bridge;

import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Optional;

public abstract class ItemProvider {
    private final String prefix;
    public ItemProvider(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    public abstract boolean belongs(ItemStack value);
    public abstract Identifier getId(ItemStack value);
    public abstract ItemStack get(Identifier id);
    public abstract Map<String, Optional<Object>> serializeData(ItemStack value, DynamicOps<?> ops);
    public abstract <D> void deserializeData(Map<String, Optional<D>> data, ItemStack value, DynamicOps<D> ops);
}
