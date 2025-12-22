package com.github.darksoulq.abyssallib.server.bridge.item;

import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.server.bridge.Provider;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Optional;

public class ItemsAdderProvider extends Provider<ItemStack> {
    public ItemsAdderProvider() {
        super("ia");
    }

    @Override
    public boolean belongs(ItemStack value) {
        return CustomStack.byItemStack(value) != null;
    }

    @Override
    public Identifier getId(ItemStack value) {
        CustomStack item = CustomStack.byItemStack(value);
        return item == null ? null : Identifier.of(item.getId());
    }

    @Override
    public ItemStack get(Identifier id) {
        CustomStack item = CustomStack.getInstance(id.toString());
        return item == null ? null : item.getItemStack();
    }

    @Override
    public Map<String, Optional<Object>> serializeData(ItemStack value, DynamicOps<?> ops) {
        return new MinecraftProvider().serializeData(value, ops);
    }

    @Override
    public <T> void deserializeData(Map<String, Optional<T>> data, ItemStack value, DynamicOps<T> ops) {
        new MinecraftProvider().deserializeData(data, value, ops);
    }
}
